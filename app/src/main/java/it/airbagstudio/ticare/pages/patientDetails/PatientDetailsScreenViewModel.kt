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
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDetailRepository: UserDetailRepository,
    private val authRepository: AuthRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository
): ViewModel() {


    var isDownloaded by mutableStateOf(false)
    var isModified by mutableStateOf(false)

    var isOnline by mutableStateOf(false)

    var badges by mutableStateOf<List<Badge>>(listOf())
    var isLoading by mutableStateOf(true)
    var isLoadingActivities by mutableStateOf(false)

    val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]
    var caseDetails by mutableStateOf<CaseDetail?>(null)
    var errorMessage by mutableStateOf<String?>(null)
    var shifts by mutableStateOf<List<OperatingShift>?>(null)
    var selectedShift by mutableStateOf<OperatingShift?>(null)
    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        throwable.printStackTrace()
        errorMessage = throwable.localizedMessage
    }
    var selectedDate by mutableLongStateOf(Calendar.getInstance().timeInMillis)

    var alerts by mutableStateOf<List<AlertItem>>(listOf())

    private var allTasksForDay by mutableStateOf<List<AgendaTask>>(listOf())

    fun downloadData(){
        requestImageRequestData = ImageRequestData(
            authRepository.getBaseURL(),
            authRepository.getToken() ?: ""
        )
        patientCod?.let { code ->

            viewModelScope.launch(coroutineExceptionHandler) {
                isOnline = offlineOnlineRepository.isOnline
                caseDetails = userDetailRepository.getCase(code).results?.firstOrNull()

                caseDetails?.let { userDetailRepository.setCurrentCase(it) }
                val caseAlerts = userDetailRepository.getCaseAlerts(code).results
                alerts = caseAlerts?.map { AlertItem(
                    colorFg = it.foreground,
                    colorBg = it.background,
                    label = it.label
                ) } ?: listOf()

                shifts = userDetailRepository.getOperatingShifts().results
                downloadBadges()

                isDownloaded = offlineOnlineRepository.patientsDownloaded.contains(patientCod)
                isModified = offlineOnlineRepository.patientsModified.contains(patientCod)

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
}