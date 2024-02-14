package it.airbagstudio.ticare.pages.splashPage

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.network.CredentialsListener
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.SyncDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.LoginRedirect
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashPageScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncDataRepository: SyncDataRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository
) : ViewModel() {

    var isLoggedIn by mutableStateOf<Boolean?>(null)
    var errorMessage by mutableStateOf<String?>(null)


    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        errorMessage = throwable.localizedMessage
        isLoggedIn = false
    }

    init {
        LoginRedirect.onCredentialRefresh = {
            authRepository.setRefreshToken(null)
            authRepository.setToken(null)
            isLoggedIn = false
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            offlineOnlineRepository.restore()
            offlineOnlineRepository.checkIfDataIsExpired()
            val _isLoggedIn =
                authRepository.getToken() != null && authRepository.getRefreshToken() != null
            if (_isLoggedIn) {
                if (offlineOnlineRepository.state.value.isOnline){
                    authRepository.getCompanyName()?.let { company ->
                        val res = syncDataRepository.syncPersistentData(company)
                        if (isLoggedIn == null && res.isSuccess) {
                            isLoggedIn = _isLoggedIn
                        }else if (res.isFailure){
                            errorMessage = res.exceptionOrNull()?.localizedMessage
                        }
                    }
                }else{
                    delay(500)
                    isLoggedIn = _isLoggedIn
                }
            }else{
                delay(500)
                isLoggedIn = false
            }

        }
    }
}