package it.airbagstudio.ticare.pages.patientsList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseInfo
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.UserListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientListScreenViewModel @Inject constructor(
    private val userListRepository: UserListRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    var isLoading by mutableStateOf(true)

    var query by mutableStateOf("")
    var patients by mutableStateOf<List<CaseInfo>?>(null)

    var companyName by mutableStateOf<String?>(null)

    var errorMessage by mutableStateOf<String?>(null)

    var coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    init {
        downloadCases()
    }

    fun downloadCases(){
        viewModelScope.launch(coroutineExceptionHandler) {
            companyName = authRepository.getCompanyName()
            isLoading = true
            val patientsResponse = userListRepository.getCaseList()
            patientsResponse.error?.let { errorResponse ->
                errorMessage = errorResponse.desc
            }
            patientsResponse.results?.let { caseInfos ->
                patients = caseInfos
            }
            isLoading = false
        }
    }
}