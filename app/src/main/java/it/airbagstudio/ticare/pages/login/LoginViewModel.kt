package it.airbagstudio.ticare.pages.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CompanyInfo
import ch.ticare.eclinic.library.entity.LoginRequest
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    var companies by mutableStateOf<List<CompanyInfo>>(listOf())

    var selectedCompany by mutableStateOf<CompanyInfo?>(null)
    var server by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var rememberMe by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var isValid = {
        selectedCompany != null && server.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()
    }

    var successLogin by mutableStateOf(false)

    init {
        viewModelScope.launch(){
            companies = userRepository.getCompaniesList().results ?: listOf()
        }
    }

    fun loginUser(){
        viewModelScope.launch(){

            isLoading = true
            delay(2000L)
            isLoading = false
            successLogin = true
        }
    }
}