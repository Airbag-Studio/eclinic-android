package it.airbagstudio.ticare.pages.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


data class SettingsPageUIState(
    val canRequestAllCases: Boolean,
    val isAllCaseActive: Boolean,
    val isDoingLogout: Boolean,
    val isLoggedOut: Boolean
)

@HiltViewModel
class SettingsPageViewModel @Inject constructor(
): ViewModel() {

    private val canRequestAllCases = MutableStateFlow(true)
    private val isAllCaseActive = MutableStateFlow(false)
    private val isDoingLogout = MutableStateFlow(false)
    private val isLoggedOut = MutableStateFlow(false)

    val uiState = combine(canRequestAllCases,isAllCaseActive,isDoingLogout,isLoggedOut){ canRequestAllCases,isAllCaseActive,isDoingLogout,isLoggedOut ->
        SettingsPageUIState(
            canRequestAllCases,
            isAllCaseActive,
            isDoingLogout,
            isLoggedOut
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly,SettingsPageUIState(false,false,false,false))

    fun setAllCaseState(value: Boolean){
        isAllCaseActive.value = value
    }

    fun requestLogout(){
        isDoingLogout.value = true
    }
}