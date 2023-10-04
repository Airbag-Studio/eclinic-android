package it.airbagstudio.ticare.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.di.CredentialListenerImpl
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(credentialListenerImpl: CredentialListenerImpl): ViewModel() {

    var backToLogin by mutableStateOf<Boolean?>(null)

    init {
        credentialListenerImpl.onCredentialRefresh = {
            backToLogin = true
        }
    }
}