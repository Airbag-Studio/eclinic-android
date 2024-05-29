package it.airbagstudio.ticare.pages.wounds.checks.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.WoundCheck
import ch.ticare.eclinic.library.entity.WoundPhoto
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.WoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.ui.components.ImageRequestData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckDetailsViewModel @Inject constructor(
    private val woundRepository: WoundRepository,
    private val authRepository: AuthRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    val woundId: String = savedStateHandle[DestinationsArgs.ID]!!
    val checkId: String = savedStateHandle[DestinationsArgs.CHECK_ID]!!

    var check by mutableStateOf<WoundCheck?>(null)
    var photos by mutableStateOf<List<WoundPhoto>>(listOf())
    var errorMessage by mutableStateOf<String?>(null)

    var isOnline by mutableStateOf(false)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorMessage = throwable.localizedMessage
    }
    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            offlineOnlineRepository.restore()
            isOnline = offlineOnlineRepository.state.value.isOnline
            val res = woundRepository.getWounds(patientCod)
            errorMessage = res.error?.desc
            val wound = res.results?.firstOrNull { it.iD == woundId.toInt() }
            check = wound?.checks?.firstOrNull { it.iD == checkId.toInt() }
            photos = wound?.photos?.filter { it.iDCheck == checkId.toInt() } ?: listOf()
        }
    }


}