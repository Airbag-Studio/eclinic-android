package it.airbagstudio.ticare.pages.login

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CompanyInfo
import ch.ticare.eclinic.library.entity.ErrorResponse
import ch.ticare.eclinic.library.entity.LoginRequest
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var companies by mutableStateOf<List<CompanyInfo>>(listOf())

    var selectedCompany by mutableStateOf<CompanyInfo?>(null)
    var server by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var rememberMe by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var isValid = {
        selectedCompany != null && server.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()
    }

    var isIpAddressValid = {
        isValidIpAddress(server)
    }

    var exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
        throwable.printStackTrace()
    }

    var successLogin by mutableStateOf(false)


    fun loginUser() {
        selectedCompany?.let { company ->
            viewModelScope.launch(exceptionHandler) {
                isLoading = true
                val loginRequest =
                    authRepository.getUUID()?.let { uuid ->
                        LoginRequest(
                            username = username,
                            password = password,
                            company = company.name,
                            group = company.group,
                            uuid = uuid
                        )

                    } ?: run {
                        LoginRequest(
                            username = username,
                            password = password,
                            company = company.name,
                            group = company.group
                        )
                    }
                val loginResponse = userRepository.login(loginRequest, rememberMe)
                loginResponse.error?.let { errorResponse: ErrorResponse ->
                    errorMessage = errorResponse.desc
                }
                loginResponse.token?.let { tokenResponse ->
                    authRepository.setCompanyGroup(company.group)
                    authRepository.setCompanyName(company.name)
                    authRepository.setUUID(tokenResponse.uuid)
                    authRepository.setToken(tokenResponse.token)
                    authRepository.setRefreshToken(tokenResponse.refreshToken)
                    successLogin = true
                }
                isLoading = false
            }
        }
    }

    fun downloadCompanies() {
        viewModelScope.launch(exceptionHandler) {
            authRepository.setBaseURL("https://$server/api/v1")
            Log.w("BASE URL",authRepository.getBaseURL())
            companies = userRepository.getCompaniesList().results ?: listOf()
        }
    }

    private fun isValidIpAddress(ipString: String): Boolean{
        return true
    }
}