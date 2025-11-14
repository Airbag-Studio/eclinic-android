package it.airbagstudio.ticare.pages.login

import android.content.Context
import android.content.RestrictionsManager
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
import ch.ticare.eclinic.library.repository.SyncDataRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import it.airbagstudio.ticare.BuildConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

data class LoginViewUIState(
    val loginData: LoginData,
    val pageState: PageState
) {
    data class LoginData(
        val isValid: Boolean = false,
        val server: String?,
        val company: String?,
        val username: String,
        val password: String,
        val rememberMe: Boolean
    )

    data class PageState(
        val companies: List<CompanyInfo> = listOf(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val successLogin: Boolean = false,
        val showSecondStep: Boolean = false
    )
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val syncDataRepository: SyncDataRepository
) : ViewModel() {

    private val companies = MutableStateFlow<List<CompanyInfo>>(listOf())

    private val selectedCompany = MutableStateFlow<CompanyInfo?>(null)
    private val server = MutableStateFlow<String?>(null)
    private val username = MutableStateFlow("")
    private val password = MutableStateFlow("")
    private val rememberMe = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val successLogin = MutableStateFlow(false)
    private val showSecondStep = MutableStateFlow(false)
    private var serverFromConfig: String? = null

    private val loginDataUiState = combine(
        server,
        selectedCompany,
        username,
        password,
        rememberMe
    ) { server, selectedCompany, username, password, rememberMe ->
        LoginViewUIState.LoginData(
            isValid = selectedCompany != null && server?.isNotEmpty() == true && username.isNotEmpty() && password.isNotEmpty(),
            server = server,
            company = selectedCompany?.name,
            username = username,
            password = password,
            rememberMe = rememberMe
        )
    }

    private val pageState = combine(
        companies,
        isLoading,
        errorMessage,
        successLogin,
        showSecondStep
    ) { companies, isLoading, errorMessage, successLogin, showSecondStep ->
        LoginViewUIState.PageState(
            companies = companies,
            isLoading = isLoading,
            errorMessage = errorMessage,
            successLogin = successLogin,
            showSecondStep = showSecondStep
        )
    }

    val uiState = combine(loginDataUiState, pageState) { loginData, pageState ->
        LoginViewUIState(
            loginData = loginData,
            pageState = pageState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LoginViewUIState(
            pageState = LoginViewUIState.PageState(),
            loginData = LoginViewUIState.LoginData(false, null, null, "", "", false)
        )
    )


    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading.value = false
        errorMessage.value = "Si è verificato un errore durante il recupero dei dati."
        throwable.printStackTrace()
    }


    fun updateData() {
        userRepository.clearAllCasesRequest()
        authRepository.getBaseURL().split("/api").firstOrNull()?.let {
            server.value = it
        }
        if (server.value.isNullOrEmpty()) {
            server.value = serverFromConfig
        }
        if (server.value.isNullOrEmpty()) { return }
        viewModelScope.launch(exceptionHandler) {
            downloadCompanies()
            if (authRepository.getRememberMe()) {
                authRepository.getBaseURL().split("/api").firstOrNull()?.let {
                    server.value = it
                }
                if (server.value.isNullOrEmpty()) {
                    server.value = serverFromConfig
                }
                downloadCompanies()
                selectedCompany.value =
                    companies.value.firstOrNull { it.name == authRepository.getCompanyName() && it.group == authRepository.getCompanyGroup() }
                username.value = authRepository.getUsername() ?: ""
                rememberMe.value = true
                if (selectedCompany.value != null && server.value != null) {
                    showSecondStep.value = true
                }
            }
        }
    }

    fun setServer(server: String?) {
        this.server.value = server
        this.companies.value = listOf()
    }

    fun setUserName(username: String) {
        this.username.value = username
    }

    fun setPassword(password: String) {
        this.password.value = password
    }

    fun setRememberMe(rememberMe: Boolean) {
        this.rememberMe.value = rememberMe
    }

    fun setCompany(company: CompanyInfo) {
        selectedCompany.value = company
    }

    fun reset() {
        errorMessage.value = null
        successLogin.value = false
    }

    fun setSecondStep(showSecondStep: Boolean) {
        this.showSecondStep.value = showSecondStep
    }

    fun loginUser() {
        selectedCompany.value?.let { company ->
            viewModelScope.launch(exceptionHandler) {
                isLoading.value = true
                val loginRequest =
                    authRepository.getUUID()?.let { uuid ->
                        LoginRequest(
                            username = username.value.trim(),
                            password = password.value.trim(),
                            company = company.name,
                            group = company.group,
                            uuid = uuid,
                            appVersion = BuildConfig.VERSION_NAME
                        )

                    } ?: run {
                        LoginRequest(
                            username = username.value.trim(),
                            password = password.value.trim(),
                            company = company.name,
                            group = company.group,
                            uuid = "",
                            appVersion = BuildConfig.VERSION_NAME
                        )
                    }
                val loginResponse = userRepository.login(loginRequest, rememberMe.value)
                loginResponse.error?.let { errorResponse: ErrorResponse ->
                    errorMessage.value = errorResponse.desc
                }
                loginResponse.token?.let { _ ->
                    authRepository.getCompanyName()?.let { company ->
                        val res = syncDataRepository.syncPersistentData(company)
                        if (res.isSuccess) {
                            successLogin.value = true
                        } else if (res.isFailure) {
                            authRepository.setToken(null)
                            authRepository.setRefreshToken(null)
                            errorMessage.value = "Si è verificato un errore durante il recupero dei dati."
                        }
                    }
                }
                isLoading.value = false
            }
        }
    }

    fun downloadCompanies() {
        companies.value = listOf()
        val _server = server.value ?: return
        viewModelScope.launch(exceptionHandler) {
            buildValidUrl(_server)?.let { validUrl ->
                Log.w("Restrictions", "valid url $validUrl")
                authRepository.setBaseURL("$validUrl/api")
                companies.value = userRepository.getCompaniesList().results ?: listOf()
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

    fun resolveRestrictions(context: Context) {
        val manager =
            context.getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager
        val restrictions = manager.applicationRestrictions
        val entries = manager.getManifestRestrictions(
            context.applicationContext.packageName
        )
        entries.firstOrNull { it.key == "base_url" }?.let { entry ->
            val baseUrl =
                if (restrictions == null || !restrictions.containsKey("base_url")) {
                    entry.selectedString
                } else {
                    restrictions.getString("base_url")
                }
            serverFromConfig = baseUrl

        }
        updateData()
    }

}