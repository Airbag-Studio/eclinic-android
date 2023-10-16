package it.airbagstudio.ticare.pages.patientDetails

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.includeTime
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDetailRepository: UserDetailRepository
): ViewModel() {


    var isLoading by mutableStateOf(false)
    var isLoadingActivities by mutableStateOf(false)

    val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]
    var caseDetails by mutableStateOf<CaseDetail?>(null)
    var errorMessage by mutableStateOf<String?>(null)
    var shifts by mutableStateOf<List<OperatingShift>?>(null)
    var selectedShift by mutableStateOf<OperatingShift?>(null)
    var pharmacologicalTasks by mutableStateOf<List<AgendaPharmacologicalTask>?>(null)

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }
    var selectedDate by mutableLongStateOf(Calendar.getInstance().timeInMillis)

    var alerts by mutableStateOf<List<AlertItem>>(listOf())

    private var allTasksForDay by mutableStateOf<List<AgendaPharmacologicalTask>>(listOf())

    init {
        patientCod?.let { code ->
            viewModelScope.launch(coroutineExceptionHandler) {
                isLoading = true
                caseDetails = userDetailRepository.getCase(code).results?.firstOrNull()

                val caseAlerts = userDetailRepository.getCaseAlerts(code).results
                alerts = caseAlerts?.map { AlertItem(
                    colorFg = it.foreground,
                    colorBg = it.background,
                    label = it.label
                ) } ?: listOf()

                shifts = userDetailRepository.getOperatingShifts().results
                downloadTasks()
                isLoading = false
            }
        }

    }

    fun downloadTasks(){
        patientCod?.let { code ->
            isLoadingActivities = true
            viewModelScope.launch {
                val date = DateFormat.format("yyyy.MM.dd", Date(selectedDate)).toString()
                allTasksForDay =
                    userDetailRepository.getAgendaForPharmacologicalTask(date, code).results ?: listOf()
                filterTasksByShift()
            }
            isLoadingActivities = false
        }
    }

    fun filterTasksByShift(){
            pharmacologicalTasks = allTasksForDay.filter { task ->
                val isInShift = if (selectedShift != null && task.expTime != null){
                    val taskTime = LocalTime.parse(task.expTime)
                    selectedShift!!.includeTime(taskTime)
                } else{
                    true
                }
                isInShift && task.execDate == null && !task.isReserve
            }

    }
}