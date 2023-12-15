package it.airbagstudio.ticare.pages.patientsList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseInfo
import ch.ticare.eclinic.library.entity.Microzone
import ch.ticare.eclinic.library.entity.Zone
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.UserListRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.ui.components.ImageRequestData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PatientListUiState(
    val companyName: String = "",
    val caseList: List<CaseInfo> = listOf(),
    val zones: List<Zone> = listOf(),
    val microZones: List<Microzone> = listOf(),
    val selectedZone: Zone? = null,
    val selectedMicrozone: Microzone? = null,
    val isRequestAllCasesAccessOn: Boolean
)

@HiltViewModel
class PatientListScreenViewModel @Inject constructor(
    private val userListRepository: UserListRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
): ViewModel() {

    var isLoading by mutableStateOf(false)
    var query by mutableStateOf("")
    //private var patients = userListRepository.getCaseList()
    private var companyName = MutableStateFlow("")
    private var errorMessage by mutableStateOf<String?>(null)
    private var zones = MutableStateFlow<List<Zone>>(listOf())
    private var microzones = MutableStateFlow<List<Microzone>>(listOf())
    private var selectedZone = MutableStateFlow<Zone?>(null)
    private var selectedMicroZone  = MutableStateFlow<Microzone?>(null)
    private var isRequestAllCasesAccessOn = userRepository.isRequestAllCasesAccessOn()

    lateinit var requestImageRequestData: ImageRequestData

    var coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    val uiState: StateFlow<PatientListUiState> = combine(zones,microzones,selectedZone,selectedMicroZone,isRequestAllCasesAccessOn){ _zones : List<Zone>,_microzones: List<Microzone>,_selectedZone : Zone?,_selectedMicrozone: Microzone?,isRequestAllCasesAccessOn ->
        isLoading = true
        val filteredMicrozones = if (_selectedZone != null){
            _microzones.filter { it.idZone == _selectedZone.id }
        }else{
            _microzones
        }
        var caseList = listOf<CaseInfo>()
        try {
            caseList = userListRepository.getRemoteCaseList(_selectedZone?.id,_selectedMicrozone?.id).results ?: listOf<CaseInfo>()
            requestImageRequestData = ImageRequestData(
                authRepository.getBaseURL(),
                authRepository.getToken() ?: ""
            )
        }catch (e: Throwable){
            isLoading = false
            errorMessage = e.localizedMessage
        }
        isLoading = false
        PatientListUiState(
            companyName = companyName.value,
            zones = _zones,
            microZones = filteredMicrozones,
            selectedMicrozone = _selectedMicrozone,
            selectedZone = _selectedZone,
            caseList = caseList,
            isRequestAllCasesAccessOn = isRequestAllCasesAccessOn
        )
    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = PatientListUiState(isRequestAllCasesAccessOn = false)
    )

    init {
        getCompanyName()
        downloadZones()
        requestImageRequestData = ImageRequestData(
            authRepository.getBaseURL(),
            authRepository.getToken() ?: ""
        )
    }

    fun getCompanyName(){
        viewModelScope.launch(coroutineExceptionHandler) {
            companyName.value = authRepository.getCompanyName() ?: ""
        }
    }

    fun downloadZones(){
        viewModelScope.launch(coroutineExceptionHandler)  {

            combine(userListRepository.getZones(),userListRepository.getUserZone(),userListRepository.getMicrozones()) { _zones,_userZone, _microzones ->
                zones.value = _zones
                microzones.value = _microzones
                if (_userZone != null){
                    selectedZone.value = _userZone
                }

            }.collect()
        }
    }

    fun setSelectedZone(zone: Zone?){
        selectedZone.value = zone
        selectedMicroZone.value = null
    }

    fun setSelectedMicrozone(microzone: Microzone?){
        selectedMicroZone.value = microzone
    }
}