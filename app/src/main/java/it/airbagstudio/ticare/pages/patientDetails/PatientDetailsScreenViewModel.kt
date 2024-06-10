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
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import ch.ticare.eclinic.library.repository.VisibilityRepository
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
import kotlinx.coroutines.flow.combine
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
    private val userRepository: UserRepository,
    private val visibilityRepository: VisibilityRepository
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
    var modifiedShift: Boolean = false
    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )
    var clinicType by mutableStateOf(ClinicType.SPITEX)

    private val _tools = userDetailRepository.getTools()
    val tools = _tools.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val permissions = visibilityRepository.getPermissions()
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
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
                if (clinicType == ClinicType.CPA && !modifiedShift) {
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

    fun getBadge(toolTag: ToolTag): Int? {
        return when (toolTag) {
            ToolTag.PharmacologicalTask -> badges.firstOrNull { it.pharmacological != null && it.pharmacological!!.badgeNumber > 0 }?.pharmacological?.badgeNumber
            ToolTag.VitalSignTask -> badges.firstOrNull { it.vitalSign != null && it.vitalSign!!.badgeNumber > 0 }?.vitalSign?.badgeNumber
            ToolTag.BloodExamTask -> badges.firstOrNull { it.bloodExam != null && it.bloodExam!!.badgeNumber > 0 }?.bloodExam?.badgeNumber
            ToolTag.PhysiotherapyTask -> badges.firstOrNull { it.physiotherapy != null && it.physiotherapy!!.badgeNumber > 0 }?.physiotherapy?.badgeNumber
            ToolTag.NursingTask -> badges.firstOrNull { it.nursing != null && it.nursing!!.badgeNumber > 0 }?.nursing?.badgeNumber
            ToolTag.EducatorTask -> badges.firstOrNull { it.educator != null && it.educator!!.badgeNumber > 0 }?.educator?.badgeNumber
            ToolTag.ErgotherapyTask -> badges.firstOrNull { it.ergotherapy != null && it.ergotherapy!!.badgeNumber > 0 }?.ergotherapy?.badgeNumber
            ToolTag.AtelierTask -> badges.firstOrNull { it.atelier != null && it.atelier!!.badgeNumber > 0 }?.atelier?.badgeNumber
            ToolTag.ActivatorTask -> badges.firstOrNull { it.activator != null && it.activator!!.badgeNumber > 0 }?.activator?.badgeNumber
            ToolTag.GenericServiceTask -> badges.firstOrNull { it.genericService != null && it.genericService!!.badgeNumber > 0 }?.genericService?.badgeNumber
            ToolTag.HomeCareServiceCarePlan -> badges.firstOrNull { it.carePlan != null && it.carePlan!!.badgeNumber > 0 }?.carePlan?.badgeNumber
            else -> null
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

    fun setSelectedTool(tool: Tool) {
        userDetailRepository.setSelectTool(tool)
    }

    fun getSelectedShiftId(): Int? {
        return if (selectedShift != null && !shifts.isNullOrEmpty()) {
            shifts!!.indexOf(selectedShift)
        } else {
            null
        }
    }
}