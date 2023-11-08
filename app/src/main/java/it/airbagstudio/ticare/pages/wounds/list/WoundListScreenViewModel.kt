package it.airbagstudio.ticare.pages.wounds.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.Wound
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.WoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class WoundListScreenUiState(
    val patientName: String?,
    val date: String,
    val shiftName: String?,
    val isLoading: Boolean,
    val wounds: List<WoundListItem>
)

@HiltViewModel
class WoundListScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val woundRepository: WoundRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val shift = userDetailRepository.getCurrentShift()
    private val date = userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT) ?: Date()
    private val currentCase = userDetailRepository.getCurrentCase()

    var errorMessage by mutableStateOf<String?>(null)
    private val isLoading = MutableStateFlow<Boolean>(false)
    private val wounds = MutableStateFlow<List<Wound>>(listOf())

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        errorMessage = throwable.localizedMessage
        isLoading.value = false
    }

    val uiState = combine(isLoading,wounds) { isLoading,wounds ->
        val items = wounds.map {
            WoundListItem(
                id = it.iD,
                name = it.appearanceDescription,
                date = it.appearanceDate.toDate("dd.MM.yyyy")?.format("dd MMMM yy") ?: "",

            )
        }
        WoundListScreenUiState(
            patientName = currentCase?.getCompleteName() ?: "",
            date = date.format("dd/MM/yyyy"),
            shiftName = shift?.name,
            isLoading = isLoading,
            wounds = items,

        )
    }.catch {
        errorMessage = it.localizedMessage
        isLoading.value = false
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly, WoundListScreenUiState(
            patientName = currentCase?.getCompleteName(),
            date = date.format("dd/MM/yyyy"),
            shiftName = shift?.name,
            isLoading = false,
            wounds = listOf()
        )
    )

    init {
        downloadWounds()
    }

    fun downloadWounds(){
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = woundRepository.getWounds(patientCod)
            wounds.value = res.results ?: listOf()
            errorMessage = res.error?.desc
            isLoading.value = false
        }
    }
}