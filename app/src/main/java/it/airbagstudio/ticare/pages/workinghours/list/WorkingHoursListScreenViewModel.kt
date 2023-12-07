package it.airbagstudio.ticare.pages.workinghours.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.EmployeeConsumption
import ch.ticare.eclinic.library.entity.EmployeeWorkingHour
import ch.ticare.eclinic.library.repository.WorkingHourRepository
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

data class WorkingHoursListScreenUIState(
    val workingHours: Map<String, List<EmployeeWorkingHour>> = mapOf(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)

@HiltViewModel
class WorkingHoursListScreenViewModel @Inject constructor(
    private val workingHourRepository: WorkingHourRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workingHours = MutableStateFlow<List<EmployeeWorkingHour>>(listOf())
    private val throwable = MutableStateFlow<Throwable?>(null)
    private val isLoading = MutableStateFlow<Boolean>(false)

    var selectedWorkingHour by mutableStateOf<EmployeeWorkingHour?>(null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, _throwable ->
        throwable.value = _throwable
        isLoading.value = false
    }

    val uiState: StateFlow<WorkingHoursListScreenUIState> = combine(workingHours,throwable,isLoading){ workingHours, throwable, isLoading ->
        WorkingHoursListScreenUIState(
            workingHours = workingHours.groupBy {
                val date = it.date.toDate("dd.MM.yyyy")
                date?.format("EEE dd MMMM") ?: it.date
            },
            errorMessage = throwable?.localizedMessage,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = WorkingHoursListScreenUIState(
            isLoading = true
        )
    )

    init {
        downloadData()
    }

    fun downloadData(){
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            workingHours.value = workingHourRepository.getWorkingHours().results ?: listOf()
            isLoading.value = false
        }
    }

    fun setSelectedConsumption(id: Int){
        selectedWorkingHour = workingHours.value.firstOrNull { it.id == id }
    }


    fun clearError(){
        throwable.value = null
    }
}