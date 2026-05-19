package it.airbagstudio.ticare.pages.falls.list

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.entity.Tool
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.FallsRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.VisibilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class FallListItem(
    val id: Int,
    val date: String,
    val hasDataToUpload: Boolean,
    val info: Map<String, String>,
)
@HiltViewModel
class FallsListViewModel @Inject constructor(
    private val fallsRepository: FallsRepository,
    private val userDetailRepository: UserDetailRepository,
    private val visibilityRepository: VisibilityRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    savedStateHandle: SavedStateHandle

) : ViewModel() {

    private val _fallsList = MutableStateFlow< List<FallListItem>>(listOf())
    val fallsList = _fallsList.asStateFlow()
    val selectedTool: Tool? = userDetailRepository.getSelectedTool()
    val title = selectedTool?.name ?: ""
    val patientCode: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    var isLoading by mutableStateOf(false)
    var shiftName by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)
    var patient by mutableStateOf<CaseDetail?>(null)
    val currentCase = userDetailRepository.getCurrentCase()

    var date by mutableStateOf<Date?>(null)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }
    var canWrite by mutableStateOf(false)
    var modifiedIds by mutableStateOf<List<String>>(listOf())

    init {
        date = userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT) ?: Date()
        shiftName = userDetailRepository.getCurrentShift()?.name
        viewModelScope.launch(coroutineExceptionHandler) {
            launch {
                visibilityRepository.getPermissions().collect { permissions ->
                    canWrite = permissions.firstOrNull { it.entity == ToolTag.Fall.name }?.canWrite ?: false
                }
            }
            isLoading = true
            patient = userDetailRepository.getCase(patientCode).results?.firstOrNull()
            downloadFalls()
            isLoading = false
        }
    }

    private suspend fun downloadFalls(){
        modifiedIds = offlineOnlineRepository.getModifiedIdForSection(patientCode,OfflineSection.NursingCourse)
        val dateParam =  DateFormat.format("yyyy.MM.dd", date).toString()

        val causes = fallsRepository.getFallCauses().first()
        _fallsList.value = fallsRepository.getFalls(patientCode, from = dateParam, to = dateParam).results?.map { fall ->
            val cause = causes.firstOrNull { it.id == fall.idCause }?.name ?: ""
            FallListItem(
                id = fall.id!!,
                date = fall.dateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)
                    ?.format("dd MMM yy HH:mm") ?: "",
                hasDataToUpload = modifiedIds.contains(fall.id.toString()),
                info = mapOf("Causa" to cause)
            )
        } ?: listOf()
    }

    fun reload(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            downloadFalls()
            isLoading = false
        }
    }
}