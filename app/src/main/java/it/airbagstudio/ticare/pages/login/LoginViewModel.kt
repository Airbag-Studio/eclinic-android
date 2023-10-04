package it.airbagstudio.ticare.pages.login

import android.content.Context
import android.util.Log
import android.webkit.URLUtil
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
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext context: Context,
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
                loginResponse.token?.let { _ ->
                    successLogin = true
                }
                isLoading = false
            }
        }
    }

    fun downloadCompanies() {
        viewModelScope.launch(exceptionHandler) {
            buildValidUrl(server)?.let { validUrl ->
                Log.w("valid url",validUrl)
                authRepository.setBaseURL("$validUrl/api/v1")
                companies = userRepository.getCompaniesList().results ?: listOf()
            }

        }

    }

    fun buildValidUrl(string: String): String? {
        val url =
            if (URLUtil.isNetworkUrl(string)) {
                URL(string)
            } else if (URLUtil.isNetworkUrl("https://$string")) {
                URL("https://$string")
            } else {
                null
            }

        url?.let { validUrl ->
            return "${validUrl.protocol}://${validUrl.host}:${validUrl.port}"
        }
        return null
    }


}