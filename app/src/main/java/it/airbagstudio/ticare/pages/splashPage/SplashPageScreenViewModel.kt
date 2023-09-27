package it.airbagstudio.ticare.pages.splashPage

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.network.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashPageScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    var isLoggedIn by mutableStateOf<Boolean?>(null)


    init {
        viewModelScope.launch {
            delay(1000L)
            Log.w("getToken",authRepository.getToken() ?: "")
            Log.w("getRefreshToken",authRepository.getRefreshToken() ?: "")
            isLoggedIn = authRepository.getToken() != null && authRepository.getRefreshToken() != null
        }
    }
}