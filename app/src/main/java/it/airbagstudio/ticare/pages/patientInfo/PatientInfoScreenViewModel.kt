package it.airbagstudio.ticare.pages.patientInfo

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
class PatientInfoScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDetailRepository: UserDetailRepository
) : ViewModel() {
    private val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]

    var caseInfo by mutableStateOf<CaseDetail?>(null)

    var errorMessage by mutableStateOf<String?>(null)

    var coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        errorMessage = throwable.localizedMessage
    }

    init {
        downloadPatientInfo()
    }

    fun downloadPatientInfo(){
        if (patientCod == null) return
        viewModelScope.launch(coroutineExceptionHandler) {
            val caseResponse = userDetailRepository.getCase(patientCod)
            caseResponse.error?.let { errorResponse ->
                errorMessage = errorResponse.desc
            }
            caseResponse.results?.let { caseResponse ->
                caseInfo = caseResponse.firstOrNull()
            }
        }
    }

}