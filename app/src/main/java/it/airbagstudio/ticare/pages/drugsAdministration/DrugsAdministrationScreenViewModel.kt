package it.airbagstudio.ticare.pages.drugsAdministration

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DrugsAdministrationScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val dateTime: String = savedStateHandle[DestinationsArgs.DATE_TIME]!!
    private val shiftStart: String = savedStateHandle[DestinationsArgs.SHIFT_START]!!
    private val shiftEnd: String = savedStateHandle[DestinationsArgs.SHIFT_END]!!

    var isLoading by mutableStateOf(false)
    var tasks by mutableStateOf<List<AgendaPharmacologicalTask>>(listOf())
    var reserves by mutableStateOf<List<AgendaPharmacologicalTask>>(listOf())
    var errorMessage by mutableStateOf<String?>(null)

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    init {

        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            val date =  DateFormat.format("yyyy.MM.dd", Date(dateTime.toLong())).toString()
            val start = LocalTime.parse(shiftStart)
            val end = LocalTime.parse(shiftEnd)
            val filteredTask = userDetailRepository.getAgendaForPharmacologicalTask(date = date,patientCod,fromCache = true).results?.filter { task ->
                val taskTime = LocalTime.parse(task.expTime)
                taskTime.isAfter(start) && taskTime.isAfter(end)
            } ?: listOf()
            tasks = filteredTask.filter { !it.isReserve }
            reserves = filteredTask.filter { it.isReserve }
            isLoading = false
        }
    }

}