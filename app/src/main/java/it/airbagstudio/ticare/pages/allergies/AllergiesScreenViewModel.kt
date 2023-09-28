package it.airbagstudio.ticare.pages.allergies

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllergiesScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDetailRepository: UserDetailRepository,
) : ViewModel() {

    private val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]

    var isLoading by mutableStateOf(true)

    var allergies by mutableStateOf<List<AllergiesItem>?>(null)

    var caseInfo by mutableStateOf<CaseDetail?>(null)

    var errorMessage by mutableStateOf<String?>(null)

    var coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    init {
        downloadAllergies()
    }

    fun downloadAllergies(){
        if (patientCod == null) return
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            caseInfo = userDetailRepository.getCase(patientCod, true).results?.firstOrNull()
            val allergiesResponse = userDetailRepository.getCaseAllergies(patientCod)
            allergiesResponse.error?.let { errorResponse ->
                errorMessage = errorResponse.desc
            }
            allergiesResponse.results?.let { allergiesResponse ->
                allergies = allergiesResponse.map { AllergiesItem(it.desc, it.isDrug) }
            }
            isLoading = false
        }
    }
}