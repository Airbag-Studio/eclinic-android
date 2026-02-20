package it.airbagstudio.ticare.pages.patientsList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.Division
import ch.ticare.eclinic.library.entity.Microzone
import ch.ticare.eclinic.library.entity.Sector
import ch.ticare.eclinic.library.entity.Zone
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.SyncDataRepository
import ch.ticare.eclinic.library.repository.UserListRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.utils.ISO_DATE_TIME
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.utils.getIconId
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class PatientListUiState(
    val isOnline: Boolean = true,
    val downloadCount: Int = 0,
    val modifiedCount: Int = 0,
    val expireDate: Date? = null,
    val companyName: String = "",
    val userName: String = "",
    val caseList: List<PatientUIState> = listOf(),
    val zones: List<Zone> = listOf(),
    val microZones: List<Microzone> = listOf(),
    val divisions: List<Division> = listOf(),
    val sectors: List<Sector> = listOf(),
    val selectedZone: Zone? = null,
    val selectedMicrozone: Microzone? = null,
    val selectedDivision: Division? = null,
    val selectedSector: Sector? = null,
    val isRequestAllCasesAccessOn: Boolean,
    val userZones: List<Zone> = listOf(),
    val clinicType: ClinicType = ClinicType.SPITEX
){
    data class PatientUIState(
        val patientCode: String,
        val birthDate: String,
        val completeName: String,
        val address: String,
        val hasDownloadedData: Boolean,
        val hasModifiedData:Boolean,
        val bed: String?,
        val genderIconId: Int,
        val photo: String?,
        val zoneName: String?,
        val microZoneName: String?
    )
}

val allDivision = Division(iD = -1, name = "Tutti", iDWarehouse = -1, ordering = -1)
val allSector = Sector(iD = -1, iDDivision = -1, name = "Tutti", ordering = -1)
@HiltViewModel
class PatientListScreenViewModel @Inject constructor(
    private val userListRepository: UserListRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val onlineRepository: OfflineOnlineRepository,
    private val syncDataRepository: SyncDataRepository
) : ViewModel() {

    private var _isContractAboutToExpire = MutableStateFlow(false)
    var isContractAboutToExpire = _isContractAboutToExpire.asStateFlow()

    var isLoading by mutableStateOf(false)
    var query by mutableStateOf("")
    var shouldUploadData by mutableStateOf(false)

    private var companyName = MutableStateFlow("")
    var errorMessage by mutableStateOf<String?>(null)
    private var zones = userListRepository.getZones()
    private var userZones = MutableStateFlow<List<Zone>>(listOf())
    private var microzones = userListRepository.getMicrozones()
    private var sectors = userListRepository.getSectors()
    private var divisions = userListRepository.getDivisions()
    private var selectedZone = MutableStateFlow<Zone?>(null)
    private var selectedMicroZone = MutableStateFlow<Microzone?>(null)
    private var selectedSector = MutableStateFlow<Sector?>(null)
    private var selectedDivision = MutableStateFlow<Division?>(null)
    private var isRequestAllCasesAccessOn = userRepository.isRequestAllCasesAccessOn()

    var requestImageRequestData: ImageRequestData

    var coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
        throwable.printStackTrace()
    }

    init {
        viewModelScope.launch {
            userListRepository.getUserZones().collect { _userZones ->
                userZones.value = _userZones
                if (_userZones.size == 1){
                    selectedZone.value = _userZones.first()
                }
            }
        }
    }
    private val caseList = combine(selectedZone,selectedMicroZone,selectedDivision,selectedSector,onlineRepository.state) { selectedZone, selectedMicroZone,selectedDivision,selectedSector, onlineRepositoryState ->
        requestImageRequestData = ImageRequestData(
            authRepository.getBaseURL(),
            authRepository.getToken() ?: ""
        )
        userListRepository.getCaseList(
            zone = selectedZone?.id,
            microzone = selectedMicroZone?.id,
            sector = if (selectedSector != null && selectedSector.iD != -1) selectedSector.iD else null,
            division = if (selectedDivision != null && selectedDivision.iD != -1) selectedDivision.iD else null
            ).results?.map {
                PatientListUiState.PatientUIState(
                    patientCode = it.code,
                    birthDate = "${it.birthday} (${it.age})",
                    completeName = it.getCompleteName(),
                    address = "${it.address}\n${it.cap} ${it.locality}",
                    photo = it.photo,
                    hasDownloadedData = onlineRepositoryState.patientsDownloaded.contains(it.code),
                    hasModifiedData = onlineRepositoryState.patientsModified.contains(it.code),
                    bed = it.bed,
                    genderIconId = it.gender.getIconId(),
                    zoneName = it.zoneName,
                    microZoneName = it.microZoneName
                )
            } ?: listOf<PatientListUiState.PatientUIState>()


    }.catch {
        isLoading = false
        errorMessage = it.localizedMessage
    }

    val clinicType = userRepository.getClinicType()

    val uiState: StateFlow<PatientListUiState> = combine(zones,microzones,selectedZone,selectedMicroZone,isRequestAllCasesAccessOn,userZones,companyName,caseList,clinicType,divisions,sectors,selectedDivision,selectedSector){ values ->
        val _zones = values[0] as List<Zone>
        val _microzones = values[1] as List<Microzone>
        val _selectedZone = values[2] as? Zone
        val _selectedMicrozone = values[3] as? Microzone
        val isRequestAllCasesAccessOn = values[4] as Boolean
        val userZones = values[5]  as List<Zone>
        val companyName = values[6] as String
        val clinicType = values[8] as ClinicType
        val sectors = values[10] as List<Sector>
        val divisions = values[9] as List<Division>
        val selectedDivision = values[11] as Division?
        val selectedSector = values[12] as Sector?
        isLoading = true
        val filteredMicrozones = if (_selectedZone != null){
            _microzones.filter { it.idZone == _selectedZone.id }
        }else{
            _microzones
        }

        val completeDivisions = listOf(allDivision) + divisions

        val filteredSectors = if (selectedDivision != null) {
            listOf(allSector) + sectors.filter { it.iDDivision == selectedDivision.iD }
        } else {
            listOf(allSector) + sectors
        }

        isLoading = false
        val syncDate = onlineRepository.state.value.syncDate?.toDate(ISO_DATE_TIME)
        val expireDate = if (syncDate != null){
            val calendar = Calendar.getInstance()
            calendar.time = syncDate
            calendar.add(Calendar.DAY_OF_YEAR,1)
            calendar.time
        }else{
            null
        }

        PatientListUiState(
            isOnline = onlineRepository.state.value.isOnline,
            downloadCount = onlineRepository.state.value.patientsDownloaded.filter { it.isNotEmpty() }.size,
            modifiedCount = onlineRepository.state.value.patientsModified.filter { it.isNotEmpty() }.size,
            expireDate = expireDate,
            companyName = companyName,
            userName = authRepository.getUserValue() ?: "",
            zones = _zones,
            microZones = filteredMicrozones,
            divisions = completeDivisions,
            sectors = filteredSectors,
            selectedMicrozone = _selectedMicrozone,
            selectedZone = _selectedZone,
            selectedDivision = selectedDivision,
            selectedSector = selectedSector,
            caseList = values[7] as List<PatientListUiState.PatientUIState>,
            isRequestAllCasesAccessOn = isRequestAllCasesAccessOn,
            userZones = userZones,
            clinicType = clinicType
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
        checkIfContractAboutToExpire()
        requestImageRequestData = ImageRequestData(
            authRepository.getBaseURL(),
            authRepository.getToken() ?: ""
        )
    }

    fun checkIfContractAboutToExpire(){
        _isContractAboutToExpire.value = userRepository.isContractAboutToExpire()
    }

    fun cancelContractAboutToExpire(){
        _isContractAboutToExpire.value = false
    }

    fun updatePatients(){
        viewModelScope.launch {
            shouldUploadData = onlineRepository.shouldUploadData()
        }
    }

    fun downloadData(){
        getCompanyName()
    }

    fun getCompanyName(){
        viewModelScope.launch(coroutineExceptionHandler) {
            companyName.value = authRepository.getCompanyName() ?: ""
        }
    }

    fun setSelectedZone(zone: Zone?) {
        selectedZone.value = zone
        selectedMicroZone.value = null
    }

    fun setSelectedMicrozone(microzone: Microzone?) {
        selectedMicroZone.value = microzone
    }

    fun setSelectedDivision(division: Division?){
        selectedDivision.value = if (division?.iD != -1) division else null
        selectedSector.value = null
    }

    fun setSelectedSector(sector: Sector?){
        selectedSector.value = sector
    }

    fun syncOfflineData(){
        viewModelScope.launch(coroutineExceptionHandler) {
            syncDataRepository.syncOfflineCreatedData().collect()
            updatePatients()
        }
    }



    fun setOffline(){
        viewModelScope.launch(coroutineExceptionHandler) {
            onlineRepository.setOffline()
        }
    }

}