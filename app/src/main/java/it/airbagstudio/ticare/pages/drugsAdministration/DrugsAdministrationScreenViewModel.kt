package it.airbagstudio.ticare.pages.drugsAdministration

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.entity.Tool
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.VisibilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.includeTime
import it.airbagstudio.ticare.utils.isSpecial
import it.airbagstudio.ticare.utils.validated
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DrugsAdministrationScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val agendaTaskRepository: AgendaTaskRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    private val visibilityRepository: VisibilityRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val dateTime: String = savedStateHandle[DestinationsArgs.DATE_TIME]!!
    private val shiftStart: String? = savedStateHandle[DestinationsArgs.SHIFT_START]
    private val shiftEnd: String? = savedStateHandle[DestinationsArgs.SHIFT_END]
    val shiftName: String = savedStateHandle[DestinationsArgs.SHIFT_NAME]!!
    val selectedTool: Tool? = userDetailRepository.getSelectedTool()
    val title = selectedTool?.name ?: ""

    var isLoading by mutableStateOf(false)
    var tasks by mutableStateOf<List<AgendaTask>>(listOf())
    var reserves by mutableStateOf<List<AgendaTask>>(listOf())
    var errorMessage by mutableStateOf<String?>(null)
    var patient by mutableStateOf<CaseDetail?>(null)
    var canWrite by mutableStateOf(false)

    var modifiedIds by mutableStateOf<List<String>>(listOf())

    var date by mutableStateOf<Date?>(null)

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    init {
        date = Date(dateTime.toLong())
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            patient = userDetailRepository.getCase(patientCod).results?.firstOrNull()
            downloadTasks()
            isLoading = false
        }
    }

    private suspend fun downloadTasks() {
        viewModelScope.launch(coroutineExceptionHandler) {
            launch {
                visibilityRepository.getPermissions().collect { permissions ->
                    canWrite = permissions.firstOrNull { it.entity == selectedTool?.toolTag?.name }?.canWrite ?: false
                }
            }
        }

        var shift : OperatingShift? = null
        if (shiftStart != null && shiftEnd != null){
            shift = OperatingShift(name = shiftName, publicName = shiftName, publicShortName = shiftName, shortName = shiftName, startTime = shiftStart, stopTime = shiftEnd)
        }

        val dateParam =  DateFormat.format("yyyy.MM.dd", date).toString()
        val expDate =  DateFormat.format("yyyy-MM-dd", date).toString()
        val allTasks = agendaTaskRepository.getAgendaTasks(ToolTag.PharmacologicalTask,date = dateParam,patientCod, null).results
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
        modifiedIds = offlineOnlineRepository.getModifiedIdForSection(patientCod,OfflineSection.Pharmacological)
        tasks = tasks.map { it.copy(reservesCount = reserves.filter { res -> res.itemPKey == it.itemPKey }.sumOf { it.expQuantity }) }
    }

    fun reloadTasks(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            downloadTasks()
            isLoading = false
        }
    }

    fun executeAll(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            tasks.filter { it.validated() }.filter { !it.isSpecial() }.filter { it.execDate != null || it.expTime != null }.forEach {task ->
                val newTask = task.copy(quantity = task.expQuantity)
                val res = agendaTaskRepository.updateAgendaTasks(newTask)
                res.error?.let {
                    errorMessage = it.desc
                }
            }

            downloadTasks()
            isLoading = false

        }
    }

}