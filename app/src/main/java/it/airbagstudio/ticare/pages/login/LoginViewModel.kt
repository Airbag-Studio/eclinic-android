package it.airbagstudio.ticare.pages.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {

    var server by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var rememberMe by mutableStateOf(false)

}