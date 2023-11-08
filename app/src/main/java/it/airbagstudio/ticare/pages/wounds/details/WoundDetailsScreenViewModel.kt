package it.airbagstudio.ticare.pages.wounds.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.Wound
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.WoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WoundDetailsScreenViewModel @Inject constructor(
    private val woundRepository: WoundRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    val woundId: String = savedStateHandle[DestinationsArgs.ID]!!

    var wound by mutableStateOf<Wound?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        errorMessage = throwable.localizedMessage
    }
    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = woundRepository.getWounds(patientCod)
            wound = res.results?.firstOrNull { it.iD == woundId.toInt() }
            errorMessage = res.error?.desc
        }
    }

    /*
    fun closeWound(description: String){
        viewModelScope.launch(coroutineExceptionHandler) {
            wound?.copy(closeDescription = description, closeDate = Date().format("dd.MM.yyyy"))?.let{
                val res = woundRepository.updateWound()
            }
        }
    }

     */
}