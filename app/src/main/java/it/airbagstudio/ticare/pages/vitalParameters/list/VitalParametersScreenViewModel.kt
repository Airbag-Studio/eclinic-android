package it.airbagstudio.ticare.pages.vitalParameters.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.AgendaTaskRepository.Companion.VITAL_SIGN_TYPE
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getExecTime
import it.airbagstudio.ticare.utils.getExpectedTime
import it.airbagstudio.ticare.utils.includeTime
import it.airbagstudio.ticare.utils.printTime
import it.airbagstudio.ticare.utils.toDate
import it.airbagstudio.ticare.utils.validated
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Date
import javax.inject.Inject

data class VitalParametersUIState(
    val items: List<VitalParameterItem> = listOf(),
    val isLoading: Boolean = false,
    val patient: CaseDetail?,
    val shift: OperatingShift?,
    val date: Date?,
    val error: String?
)

@HiltViewModel
class VitalParametersScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val agendaTaskRepository: AgendaTaskRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var selectedTask by mutableStateOf<AgendaTask?>(null)
    var selectedVitalSignCode by mutableStateOf<String?>(null)

    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val case = userDetailRepository.getCurrentCase()
    private val date = userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT)
    private val shift = userDetailRepository.getCurrentShift()
    private val modifiedIds = MutableStateFlow<List<String>>(listOf())
    private val isLoading = MutableStateFlow<Boolean>(false)
    private val throwable = MutableStateFlow<Throwable?>(null)
    private val tasks = MutableStateFlow<List<AgendaTask>>(listOf())
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, _throwable ->
            throwable.value = _throwable
        }

    val uiState: StateFlow<VitalParametersUIState> =
        combine(isLoading, throwable, tasks,modifiedIds) { _isLoading, _throwable, _tasks,modifiedIds ->
            val filteredTasks = if (shift != null) {
                _tasks.filter {
                    shift?.includeTime(
                        it.getExpectedTime() ?: LocalTime.now()
                    ) == true
                }
            } else {
                _tasks
            }
            val items = filteredTasks.map {
                val time = it.getExecTime() ?: it.getExpectedTime()
                val timeFormatted = time?.printTime() ?: ""
                val executed = it.getExecTime() != null
                VitalParameterItem(
                    name = it.typeDescription,
                    typeMsmUnit = it.typeMsmUnit,
                    quantity = it.value ?: "",
                    time = timeFormatted,
                    executed = executed,
                    isConfirmed = it.validated(),
                    item = it,
                    hasDataToUpload = modifiedIds.contains(it.pkey.toString())
                )
            }
            VitalParametersUIState(
                items = items,
                isLoading = _isLoading,
                patient = case,
                shift = shift,
                date = date,
                error = _throwable?.localizedMessage
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VitalParametersUIState(
                isLoading = true,
                patient = case,
                shift = shift,
                date = date,
                error = null
            )
        )

    fun clearError() {
        throwable.value = null
    }

    init {
        downloadData()
    }

    fun downloadData(){
        isLoading.value = false
        viewModelScope.launch(coroutineExceptionHandler) {
            modifiedIds.value = offlineOnlineRepository.getModifiedIdForSection(patientCod,OfflineSection.VitalSign)
            val _tasks = agendaTaskRepository.getAgendaTasks(
                ToolTag.VitalSignTask,
                date?.format("yyyy.MM.dd") ?: "",
                patientCod,
                null
            ).results ?: listOf()
            tasks.value = _tasks
            isLoading.value = false
        }
    }
}