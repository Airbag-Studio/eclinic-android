package it.airbagstudio.ticare.pages.consumptions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.EmployeeConsumption
import ch.ticare.eclinic.library.repository.ConsumptionRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConsumptionListUIState(
    val consumptions: Map<String, List<EmployeeConsumption>> = mapOf(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val clinicType: ClinicType? = null
)

@HiltViewModel
class ConsumptionListViewModel @Inject constructor(
    private val consumptionRepository: ConsumptionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val consumptions = MutableStateFlow<List<EmployeeConsumption>>(listOf())
    private val throwable = MutableStateFlow<Throwable?>(null)
    private val isLoading = MutableStateFlow<Boolean>(false)
    private val clinicType = userRepository.getClinicType()

    var selectedConsumption by mutableStateOf<EmployeeConsumption?>(null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _throwable ->
        throwable.value = _throwable
        isLoading.value = false
    }

    val uiState: StateFlow<ConsumptionListUIState> = combine(consumptions,throwable,isLoading,clinicType){ consumptions, throwable, isLoading,clinicType ->
        ConsumptionListUIState(
            consumptions = consumptions.groupBy {
                val date = it.date.toDate("dd.MM.yyyy")
                date?.format("EEE dd MMMM") ?: it.date
            },
            errorMessage = throwable?.localizedMessage,
            isLoading = isLoading,
            clinicType = clinicType
        )
    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = ConsumptionListUIState(
            isLoading = true
        )
    )

    init {
        downloadData()
    }

    fun downloadData(){
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            consumptions.value = consumptionRepository.getConsumptions().results ?: listOf()
            isLoading.value = false
        }
    }

    fun setSelectedConsumption(id: Int){
        selectedConsumption = consumptions.value.firstOrNull { it.id == id }
    }


    fun clearError(){
        throwable.value = null
    }
}