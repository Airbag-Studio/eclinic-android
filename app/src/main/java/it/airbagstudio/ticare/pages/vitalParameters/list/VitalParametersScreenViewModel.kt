package it.airbagstudio.ticare.pages.vitalParameters.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import javax.inject.Inject

data class VitalParametersUIState(
    val items: List<VitalParameterItem> = listOf(),
    val isLoading: Boolean = false,
    val patient: CaseDetail?,
    val shift: OperatingShift?,
    val date: Date?
)

@HiltViewModel
class VitalParametersScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val case = userDetailRepository.getCurrentCase()
    private val date = userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT)
    private val shift = userDetailRepository.getCurrentShift()
    private val isLoading = MutableStateFlow<Boolean>(false)
    private val throwable = MutableStateFlow<Throwable?>(null)

    val uiState: StateFlow<VitalParametersUIState> = combine(isLoading,throwable){ _isLoading,_throwable ->
        VitalParametersUIState(
            isLoading = _isLoading,
            patient = case,
            shift = shift,
            date = date
        )
    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = VitalParametersUIState(
            isLoading = true,
            patient = case,
            shift = shift,
            date = date
        )
    )

    init {
        isLoading.value = false
    }
}