package it.airbagstudio.ticare.pages.splashPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject


@HiltViewModel
class SplashPageScreenViewModel @Inject constructor(): ViewModel() {

    var loginSuccess by mutableStateOf(false)


    init {
        viewModelScope.launch {
            delay(1000L)
            loginSuccess = true
        }
    }
}