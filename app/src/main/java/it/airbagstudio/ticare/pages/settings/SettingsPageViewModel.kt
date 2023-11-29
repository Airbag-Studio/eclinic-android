package it.airbagstudio.ticare.pages.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SettingsPageUIState(
    val canRequestAllCases: Boolean,
    val isAllCaseActive: Boolean,
    val isDoingLogout: Boolean,
    val isLoggedOut: Boolean
)

@HiltViewModel
class SettingsPageViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val canRequestAllCases = userRepository.canRequestAllCases()
    private val isAllCaseActive = userRepository.isRequestAllCasesAccessOn()
    private val isDoingLogout = MutableStateFlow(false)
    private val isLoggedOut = MutableStateFlow(false)

    var errorMessage by mutableStateOf<String?>(null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        errorMessage = throwable.localizedMessage
    }

    val uiState = combine(canRequestAllCases,isAllCaseActive,isDoingLogout,isLoggedOut){ canRequestAllCases,isAllCaseActive,isDoingLogout,isLoggedOut ->
        SettingsPageUIState(
            canRequestAllCases,
            isAllCaseActive,
            isDoingLogout,
            isLoggedOut
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly,SettingsPageUIState(false,false,false,false))

    fun clearAllCasesRequest(){
        userRepository.clearAllCasesRequest()
    }

    fun requestLogout(){
        isDoingLogout.value = true
        userRepository.logout()
        isLoggedOut.value = true
    }

    fun requestAllCasesAccess(message: String){
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = userRepository.requestAllCasesAccess(message)
            errorMessage = res.error?.desc
        }
    }
}