package it.airbagstudio.ticare.pages.drugsAdministration

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.repository.CasePharmacologicalTaskRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.includeTime
import it.airbagstudio.ticare.utils.validated
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DrugsAdministrationScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val casePharmacologicalTaskRepository: CasePharmacologicalTaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val dateTime: String = savedStateHandle[DestinationsArgs.DATE_TIME]!!
    private val shiftStart: String? = savedStateHandle[DestinationsArgs.SHIFT_START]
    private val shiftEnd: String? = savedStateHandle[DestinationsArgs.SHIFT_END]
    val shiftName: String = savedStateHandle[DestinationsArgs.SHIFT_NAME]!!

    var isLoading by mutableStateOf(false)
    var tasks by mutableStateOf<List<AgendaPharmacologicalTask>>(listOf())
    var reserves by mutableStateOf<List<AgendaPharmacologicalTask>>(listOf())
    var errorMessage by mutableStateOf<String?>(null)
    var patient by mutableStateOf<CaseDetail?>(null)

    var date by mutableStateOf<Date?>(null)

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    init {
        date = Date(dateTime.toLong())
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            patient = userDetailRepository.getCase(patientCod).results?.firstOrNull()
            downloadTasks(true)
            isLoading = false
        }
    }

    private suspend fun downloadTasks(fromCache: Boolean){
        var shift : OperatingShift? = null
        if (shiftStart != null && shiftEnd != null){
            shift = OperatingShift(name = shiftName, publicName = shiftName, publicShortName = shiftName, shortName = shiftName, startTime = shiftStart, stopTime = shiftEnd)
        }

        val dateParam =  DateFormat.format("yyyy.MM.dd", date).toString()
        val expDate =  DateFormat.format("yyyy-MM-dd", date).toString()
        val allTasks = casePharmacologicalTaskRepository.getAgendaForPharmacologicalTask(date = dateParam,patientCod).results
        tasks = allTasks?.filter { task ->
            if(task.expTime != null){
                val taskTime = LocalTime.parse(task.expTime)
                (shift?.includeTime(taskTime) ?: true) && !task.isReserve
            } else {
                !task.isReserve
            }
        } ?: listOf()

        reserves = allTasks?.filter { task ->
            task.isReserve && if (task.expTime != null) {
                val taskTime = LocalTime.parse(task.expTime)
                (shift?.includeTime(taskTime) ?: true)
            }  else {
                task.expDate == expDate
            }
        } ?: listOf()

        tasks = tasks.map { it.copy(reservesCount = reserves.filter { res -> res.itemPKey == it.itemPKey }.sumOf { it.expQuantity }) }
    }

    fun reloadTasks(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            downloadTasks(false)
            isLoading = false
        }
    }

    fun executeAll(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            val newTasks = tasks.filter { it.validated() }.map {task ->
                //val execDate = DateFormat.format("yyyy-MM-dd", Date()).toString()
                //val execTime = DateFormat.format("HH:mm:ss.000", Date()).toString()
                task.copy(quantity = task.expQuantity)
            }
            val res = casePharmacologicalTaskRepository.updatePharmacologicalTasks(newTasks)
            res.error?.let {
                errorMessage = it.desc
            }
            downloadTasks(false)
            isLoading = false
        }
    }

}