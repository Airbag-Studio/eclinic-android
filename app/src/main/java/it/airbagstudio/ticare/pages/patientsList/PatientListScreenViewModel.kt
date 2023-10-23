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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PatientListUiState(
    val companyName: String = "",
    val caseList: List<CaseInfo> = listOf(),
    val isLoading:Boolean = false,
    val zones: List<Zone> = listOf(),
    val microZones: List<Microzone> = listOf(),
    val selectedZone: Zone? = null,
    val selectedMicrozone: Microzone? = null
)

@HiltViewModel
class PatientListScreenViewModel @Inject constructor(
    private val userListRepository: UserListRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    private var isLoading by mutableStateOf(true)
    var query by mutableStateOf("")
    private var patients = MutableStateFlow<List<CaseInfo>>(listOf())
    private var companyName = MutableStateFlow("")
    private var errorMessage by mutableStateOf<String?>(null)
    private var zones = MutableStateFlow<List<Zone>>(listOf())
    private var microzones = MutableStateFlow<List<Microzone>>(listOf())
    private var selectedZone = MutableStateFlow<Zone?>(null)
    private var selectedMicroZone  = MutableStateFlow<Microzone?>(null)

    var coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    val uiState: StateFlow<PatientListUiState> = combine(zones,microzones,selectedZone,selectedMicroZone,patients){ _zones : List<Zone>,_microzones: List<Microzone>,_selectedZone : Zone?,_selectedMicrozones: Microzone?,_cases ->
        val filteredMicrozones = if (_selectedZone != null){
            _microzones.filter { it.idZone == _selectedZone.id }
        }else{
            _microzones
        }
        PatientListUiState(
            companyName = companyName.value,
            isLoading = false,
            zones = _zones,
            microZones = filteredMicrozones,
            selectedMicrozone = _selectedMicrozones,
            selectedZone = _selectedZone,
            caseList = _cases
        )
    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = PatientListUiState(isLoading = true)
    )

    init {
        downloadCases()
        downloadZones()
    }

    fun downloadCases(){
        viewModelScope.launch(coroutineExceptionHandler) {
            companyName.value = authRepository.getCompanyName() ?: ""
            isLoading = true
            userListRepository.getCaseList().collect {
                patients.value = it
                isLoading = false
            }

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