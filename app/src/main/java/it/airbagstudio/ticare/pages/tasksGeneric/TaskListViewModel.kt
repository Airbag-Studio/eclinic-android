package it.airbagstudio.ticare.pages.tasksGeneric

import android.text.format.DateFormat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.entity.TaskType
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.getExecTime
import it.airbagstudio.ticare.utils.getExpectedTime
import it.airbagstudio.ticare.utils.getLabelId
import it.airbagstudio.ticare.utils.printTime
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class TaskListUIState(
    val sectionTitleId: Int = R.string.empty,
    val services: List<TaskListItem> = listOf(),
    val selectedDate: String = "",
    val selectedShift: OperatingShift? = null,
    val patientName: String = "",
    val isLoading: Boolean = true,
    val taskType: ToolTag = ToolTag.GenericServiceTask,
    val patientCode: String,
    val typesForTask: List<TaskType> = listOf()
)


@HiltViewModel
class TaskListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDetailRepository: UserDetailRepository,
    private val agendaTaskRepository: AgendaTaskRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository
) : ViewModel() {
    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    val taskType: String = savedStateHandle[DestinationsArgs.TASK_TYPE]!!


    private val _uiState =
        MutableStateFlow<TaskListUIState>(TaskListUIState(patientCode = patientCod))

    val uiState = _uiState.asStateFlow()

    fun downloadData() {
        val toolTag = ToolTag.valueOf(taskType)
        viewModelScope.launch {
            val patient = userDetailRepository.getCurrentCase()
            val date = userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT) ?: Date()
            val dateParam = DateFormat.format("yyyy.MM.dd", date).toString()
            val taskTypes = agendaTaskRepository.getActivityTypeTypes(toolTag.name)
            val modifiedIds = offlineOnlineRepository.getModifiedIdForSection(
                patientCod,
                toolTag.getOfflineSection()
            )
            val tasks = agendaTaskRepository.getAgendaTasks(toolTag, dateParam, patientCod).results
                ?: listOf()
            val taskListItems = tasks.map {
                val time = it.getExecTime() ?: it.getExpectedTime()
                val timeFormatted = time?.printTime() ?: ""
                val executed = it.getExecTime() != null

                TaskListItem(
                    name = it.typeDescription,
                    time = timeFormatted,
                    executed = executed,
                    hasDataToUpload = modifiedIds.contains(it.pkey.toString()),
                    task = it
                )
            }
            _uiState.value = TaskListUIState(
                taskType = toolTag,
                patientName = patient?.name ?: "",
                selectedDate = DateFormat.format("dd/MM/yyyy", date).toString(),
                selectedShift = null,
                sectionTitleId = toolTag.getLabelId(),
                services = taskListItems,
                patientCode = patientCod,
                typesForTask = taskTypes
            )
        }
        //val patientName = "Elisa Santoro"

    }
}