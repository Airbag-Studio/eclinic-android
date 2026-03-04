package it.airbagstudio.ticare.pages.medicalDiagnoses

import android.provider.Settings.System.canWrite
import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.MedicalDiagnosis
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.MedicalDiagnosesRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.VisibilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.utils.io.printStack
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.pages.patientDetails.form.ui.home.HomeUiState
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class MedicalDiagnosesUiState(
    val date: String,
    val shift: String?,
    val patientCode: String,
    val medicalDiagnoses: List<MedicalDiagnosesListItem> = emptyList(),
)

@HiltViewModel
class MedicalDiagnosesViewModel @Inject constructor(
    private val medicalDiagnosisRepository: MedicalDiagnosesRepository,
    private val userDetailRepository: UserDetailRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    private val visibilityRepository: VisibilityRepository,
    savedStateHandle: SavedStateHandle

) : ViewModel() {
    val patientCode: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!

    private val _uiState = MutableStateFlow<MedicalDiagnosesUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableStateFlow<Error?>(null)
    val error = _error.asStateFlow()
    var canWrite by mutableStateOf(false)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStack()
        _error.value = Error(throwable)
    }

    fun downloadData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            launch {
                visibilityRepository.getPermissions().collect { permissions ->
                    canWrite =
                        permissions.firstOrNull { it.entity == ToolTag.MedicalDiagnosis.name }?.canWrite
                            ?: false
                }
            }
            val patient = userDetailRepository.getCurrentCase()
            val shift = userDetailRepository.getCurrentShift()
            val modifiedIds = offlineOnlineRepository.getModifiedIdForSection(
                patientCode,
                OfflineSection.MedicalDiagnosis
            )
            val date = userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT) ?: Date()
            val dateParam = DateFormat.format("yyyy.MM.dd HH:mm", date).toString()
            val medicalDiagnosesResponse = medicalDiagnosisRepository.getMedicalDiagnoses(
                patientCode,
            )
            val medicalDiagnoses = medicalDiagnosesResponse.results?.map { medicalDiagnosis ->
                MedicalDiagnosesListItem(
                    openDate = medicalDiagnosis.openDate,
                    description = medicalDiagnosis.desc,
                    operatorName = medicalDiagnosis.openUser,
                    hasDataToUpload = modifiedIds.contains(medicalDiagnosis.id.toString())
                )
            }
            if(medicalDiagnosesResponse.error != null) {
                _error.value = Error(medicalDiagnosesResponse.error?.desc ?: "Si è verificato un errore")
            }
            _uiState.value = MedicalDiagnosesUiState(
                date = dateParam,
                shift = shift?.name,
                patientCode = patientCode,
                medicalDiagnoses = medicalDiagnoses ?: listOf()
            )
        }
    }
}