package it.airbagstudio.ticare.pages.patientsList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseInfo
import ch.ticare.eclinic.library.entity.Microzone
import ch.ticare.eclinic.library.entity.WoundPhoto
import ch.ticare.eclinic.library.entity.Zone
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserListRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.utils.ISO_DATE_TIME
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class PatientListUiState(
    val isOnline: Boolean = true,
    val downloadCount: Int = 0,
    val modifiedCount: Int = 0,
    val expireDate: Date? = null,
    val companyName: String = "",
    val caseList: List<PatientUIState> = listOf(),
    val zones: List<Zone> = listOf(),
    val microZones: List<Microzone> = listOf(),
    val selectedZone: Zone? = null,
    val selectedMicrozone: Microzone? = null,
    val isRequestAllCasesAccessOn: Boolean,
    val userZones: List<Zone> = listOf(),
){
    data class PatientUIState(
        val patientCode: String,
        val birthDate: String,
        val completeName: String,
        val address: String,
        val hasDownloadedData: Boolean,
        val hasModifiedData:Boolean,
        val photo: String?
    )
}

@HiltViewModel
class PatientListScreenViewModel @Inject constructor(
    private val userListRepository: UserListRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val onlineRepository: OfflineOnlineRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
    var query by mutableStateOf("")

    private var isOnline = MutableStateFlow(true)
    private var companyName = MutableStateFlow("")
    var errorMessage by mutableStateOf<String?>(null)
    private var zones = MutableStateFlow<List<Zone>>(listOf())
    private var userZones = MutableStateFlow<List<Zone>>(listOf())
    private var microzones = MutableStateFlow<List<Microzone>>(listOf())
    private var selectedZone = MutableStateFlow<Zone?>(null)
    private var selectedMicroZone = MutableStateFlow<Microzone?>(null)
    private var isRequestAllCasesAccessOn = userRepository.isRequestAllCasesAccessOn()
    private val  patientsDownloaded = MutableStateFlow<Set<String>>(setOf())
    private val  patientsModified = MutableStateFlow<Set<String>>(setOf())

    lateinit var requestImageRequestData: ImageRequestData

    var coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    private val caseList = combine(selectedZone,selectedMicroZone,isOnline,patientsDownloaded,patientsModified) { selectedZone, selectedMicroZone,isOnline,patientsDownloaded,patientsModified ->
        requestImageRequestData = ImageRequestData(
            authRepository.getBaseURL(),
            authRepository.getToken() ?: ""
        )
         userListRepository.getCaseList(selectedZone?.id,selectedMicroZone?.id).results?.map {
                PatientListUiState.PatientUIState(
                    patientCode = it.code,
                    birthDate = "${it.birthday} (${it.age})",
                    completeName = it.getCompleteName(),
                    address = "${it.address}\n${it.cap} ${it.locality}",
                    photo = it.photo,
                    hasDownloadedData = patientsDownloaded.contains(it.code),
                    hasModifiedData = patientsModified.contains(it.code)
                )
            } ?: listOf<PatientListUiState.PatientUIState>()


    }.catch {
        isLoading = false
        errorMessage = it.localizedMessage
    }

    val uiState: StateFlow<PatientListUiState> = combine(zones,microzones,selectedZone,selectedMicroZone,isRequestAllCasesAccessOn,userZones,companyName,caseList){ values ->
        val _zones = values[0] as List<Zone>
        val _microzones = values[1] as List<Microzone>
        val _selectedZone = values[2] as? Zone
        val _selectedMicrozone = values[3] as? Microzone
        val isRequestAllCasesAccessOn = values[4] as Boolean
        val userZones = values[5]  as List<Zone>
        val companyName = values[6] as String
        isLoading = true

        if (userZones.isNotEmpty()){
            selectedZone.value = userZones.first()
        }
        val filteredMicrozones = if (_selectedZone != null){
            _microzones.filter { it.idZone == _selectedZone.id }
        }else{
            _microzones
        }

        isLoading = false
        val syncDate = onlineRepository.syncDate?.toDate(ISO_DATE_TIME)
        val expireDate = if (syncDate != null){
            val calendar = Calendar.getInstance()
            calendar.time = syncDate
            calendar.add(Calendar.DAY_OF_YEAR,1)
            calendar.time
        }else{
            null
        }

        PatientListUiState(
            isOnline = onlineRepository.isOnline,
            downloadCount = onlineRepository.patientsDownloaded.filter { it.isNotEmpty() }.size,
            modifiedCount = onlineRepository.patientsModified.filter { it.isNotEmpty() }.size,
            expireDate = expireDate,
            companyName = companyName,
            zones = _zones,
            microZones = filteredMicrozones,
            selectedMicrozone = _selectedMicrozone,
            selectedZone = _selectedZone,
            caseList = values[7] as List<PatientListUiState.PatientUIState>,
            isRequestAllCasesAccessOn = isRequestAllCasesAccessOn,
            userZones = userZones
        )
    }.catch {
        isLoading = false
        errorMessage = it.localizedMessage
    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = PatientListUiState(isRequestAllCasesAccessOn = false)
    )

    init {
        downloadData()
        requestImageRequestData = ImageRequestData(
            authRepository.getBaseURL(),
            authRepository.getToken() ?: ""
        )
        isOnline.value = onlineRepository.isOnline
        patientsModified.value = onlineRepository.patientsModified
        patientsDownloaded.value = onlineRepository.patientsDownloaded
    }

    fun updatePatients(){
        patientsModified.value = onlineRepository.patientsModified
        patientsDownloaded.value = onlineRepository.patientsDownloaded
    }

    fun downloadData(){
        getCompanyName()
        downloadZones()
    }

    fun getCompanyName(){
        viewModelScope.launch(coroutineExceptionHandler) {
            companyName.value = authRepository.getCompanyName() ?: ""
        }
    }

    fun downloadZones(){
        viewModelScope.launch(coroutineExceptionHandler)  {
            zones.value = listOf()
            combine(userListRepository.getZones(),userListRepository.getUserZones(),userListRepository.getMicrozones()) { _zones,_userZones, _microzones ->
                zones.value = _zones
                microzones.value = _microzones
                userZones.value = _userZones
                if (_userZones.isNotEmpty()){
                    selectedZone.value = _userZones.first()
                }

            }.collect()
        }
    }

    fun setSelectedZone(zone: Zone?) {
        selectedZone.value = zone
        selectedMicroZone.value = null
    }

    fun setSelectedMicrozone(microzone: Microzone?) {
        selectedMicroZone.value = microzone
    }

    fun setOnline(){
        viewModelScope.launch(coroutineExceptionHandler) {
            onlineRepository.setOnline()
            isOnline.value = onlineRepository.isOnline
        }
    }

    fun setOffline(){
        viewModelScope.launch(coroutineExceptionHandler) {
            onlineRepository.setOffline()
            isOnline.value = onlineRepository.isOnline
        }
    }

}