package it.airbagstudio.ticare.pages.wounds.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CloseWound
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.entity.Wound
import ch.ticare.eclinic.library.entity.WoundSave
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.WoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WoundDetailsScreenViewModel @Inject constructor(
    private val woundRepository: WoundRepository,
    private val authRepository: AuthRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    val woundId: String = savedStateHandle[DestinationsArgs.ID]!!
    val genderId: Int = savedStateHandle.get<String>(DestinationsArgs.GENDER_ID)!!.toInt()

    var wound by mutableStateOf<Wound?>(null)
    var errorMessage by mutableStateOf<String?>(null)
    var isOnline by mutableStateOf(false)
    var modifiedIds by mutableStateOf<List<String>>(listOf())

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        errorMessage = throwable.localizedMessage
    }
    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )
    var closedWound by mutableStateOf(false)

    init {
        reloadWound()
    }

    fun reloadWound() {
        viewModelScope.launch(coroutineExceptionHandler) {
            modifiedIds = offlineOnlineRepository.getModifiedIdForSection(patientCod,OfflineSection.WoundChecks)
            offlineOnlineRepository.restore()
            isOnline = offlineOnlineRepository.state.value.isOnline
            val res = woundRepository.getWound(patientCod,woundId.toInt())
            wound = res.results?.firstOrNull()
            errorMessage = res.error?.desc
        }
    }

    fun closeWound(description: String){
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = woundRepository.closeWound(
                patientCod,
                CloseWound(
                    woundId.toInt(),
                    Date().format("yyyy.MM.dd"),
                    description
                )
            )
            closedWound = res.results?.isNotEmpty() == true
            errorMessage = res.error?.desc
        }
    }

}