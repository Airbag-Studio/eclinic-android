package it.airbagstudio.ticare.pages.patientDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.entity.Badge
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.entity.Tool
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.isCurrent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDetailRepository: UserDetailRepository,
    private val authRepository: AuthRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    private val userRepository: UserRepository
): ViewModel() {


    var isDownloaded by mutableStateOf(false)
    var isModified by mutableStateOf(false)

    var isOnline by mutableStateOf(false)

    var badges by mutableStateOf<List<Badge>>(listOf())
    var isLoading by mutableStateOf(true)

    val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]
    var caseDetails by mutableStateOf<CaseDetail?>(null)
    var errorMessage by mutableStateOf<String?>(null)
    var shifts by mutableStateOf<List<OperatingShift>?>(null)
    var selectedShift by mutableStateOf<OperatingShift?>(null)
    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )
    var clinicType by mutableStateOf(ClinicType.SPITEX)

    private val _tools = userDetailRepository.getTools()
    val tools = _tools.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        throwable.printStackTrace()
        errorMessage = throwable.localizedMessage
    }
    var selectedDate by mutableLongStateOf(Calendar.getInstance().timeInMillis)

    var alerts by mutableStateOf<List<AlertItem>>(listOf())

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            userRepository.getClinicType().collect {
                if (it != null) clinicType = it
            }
        }
    }

    fun downloadData(){
        requestImageRequestData = ImageRequestData(
            authRepository.getBaseURL(),
            authRepository.getToken() ?: ""
        )
        patientCod?.let { code ->

            viewModelScope.launch(coroutineExceptionHandler) {
                isOnline = offlineOnlineRepository.state.value.isOnline
                caseDetails = userDetailRepository.getCase(code).results?.firstOrNull()

                caseDetails?.let { userDetailRepository.setCurrentCase(it) }
                val caseAlerts = userDetailRepository.getCaseAlerts(code).results
                alerts = caseAlerts?.map { AlertItem(
                    colorFg = it.foreground,
                    colorBg = it.background,
                    label = it.label
                ) } ?: listOf()

                shifts = userDetailRepository.getOperatingShifts().results
                if (clinicType == ClinicType.CPA && selectedShift == null) {
                    shifts?.firstOrNull { it.isCurrent() }?.let {
                        selectedShift = it
                    }
                }
                downloadBadges()

                isDownloaded = offlineOnlineRepository.state.value.patientsDownloaded.contains(patientCod)
                isModified = offlineOnlineRepository.state.value.patientsModified.contains(patientCod)

                isLoading = false
            }
        }


    }

    fun downloadBadges(){
        userDetailRepository.setSelectDate(Date(selectedDate).format(SERVER_DATE_FORMAT))
        userDetailRepository.setCurrentShift(selectedShift)
        if (patientCod != null) {
            viewModelScope.launch(coroutineExceptionHandler) {
                val shiftIndex: Int? =
                    if (selectedShift != null && !shifts.isNullOrEmpty()) {
                        shifts!!.indexOf(selectedShift)
                    } else {
                        null
                    }
                val res = userDetailRepository.getBadges(Date(selectedDate).format("yyyy.MM.dd HH:mm"),patientCod,shiftIndex)
                badges = res.results ?: listOf()
            }

        }
    }

    fun getSelectedShiftId(): Int? {
        return if (selectedShift != null && !shifts.isNullOrEmpty()) {
            shifts!!.indexOf(selectedShift)
        } else {
            null
        }
    }
}