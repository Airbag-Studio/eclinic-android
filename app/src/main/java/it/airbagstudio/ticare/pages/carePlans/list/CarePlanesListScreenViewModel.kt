package it.airbagstudio.ticare.pages.carePlans.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CarePlanesListScreenUIState(
    val isLoading: Boolean,
    val patientName:String,
    val errorMessage: String?,
    val items: List<CarePlanesListItem>
)

@HiltViewModel
class CarePlanesListScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val patientName = userDetailRepository.getCurrentCase()?.name ?: ""
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    var uiState = combine(isLoading, errorMessage) { isLoading, errorMessage ->
        CarePlanesListScreenUIState(
            isLoading = true,
            patientName = patientName,
            errorMessage = null,
            items = listOf()
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CarePlanesListScreenUIState(
            isLoading = true,
            patientName = patientName,
            errorMessage = null,
            items = listOf()
        )
    )
}