package it.airbagstudio.ticare.pages.tasksGeneric.createEdit

import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.entity.TaskType
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.entity.UnscheduledTask
import ch.ticare.eclinic.library.entity.UnscheduledTaskFields
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class TaskCreateEditScreenUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val taskActivityType: TaskType? = null,
    val isEditingEnabled: Boolean,
    val task: TaskUiState
){
    data class TaskUiState(
        val dateTime: Date = Date(),
        val duration: Int? = null,
        val notes: String = "",
        val showInDiary: Boolean = false,
        val notExecuted: Boolean = false,
        val isValid: Boolean = false,
        val generalNote: String = ""
    )
}

@HiltViewModel
class TaskCreateEditViewModel @Inject constructor(
    private val agendaTaskRepository: AgendaTaskRepository,
    private val userRepository: UserRepository
): ViewModel() {

    var canWrite = false
    var agendaTask: AgendaTask? = null
    lateinit var caseCode: String
    lateinit var taskType: ToolTag
    var userId: Int = 0


    private val errorMessage = MutableStateFlow<String?>(null)
    private val isLoading = MutableStateFlow(false)
    private val isSuccess = MutableStateFlow(false)
    //TASK
    private val dateTime = MutableStateFlow(Date())
    private val duration = MutableStateFlow<Int?>(null)
    private val notes = MutableStateFlow<String>("")
    private val generalNote = MutableStateFlow<String>("")
    private val showInDiary = MutableStateFlow(false)
    private val notExecuted = MutableStateFlow(false)
    private val taskActivityType = MutableStateFlow<TaskType?>(null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading.value = false
        errorMessage.value = throwable.localizedMessage
        throwable.printStackTrace()
    }

    private val task = combine(dateTime,duration,notes,showInDiary,notExecuted,generalNote){ data ->
        val dateTime = data[0] as Date
        val duration = data[1] as? Int
        val notes = data[2] as String
        val showInDiary = data[3] as Boolean
        val notExecuted = data[4] as Boolean
        val generalNote = data[5] as String
        TaskCreateEditScreenUIState.TaskUiState(
            dateTime = dateTime,
            duration = duration,
            notes = notes,
            showInDiary = showInDiary,
            notExecuted = notExecuted,
            isValid = !notExecuted || notes.isNotEmpty(),
            generalNote = generalNote
        )
    }

    val uiState = combine(task,isLoading,errorMessage,isSuccess,taskActivityType){ task,isLoading,errorMessage,isSuccess,taskActivityType ->
        TaskCreateEditScreenUIState(
            isLoading = isLoading,
            errorMessage = errorMessage,
            isSuccess = isSuccess,
            taskActivityType,
            isEditingEnabled = canWrite,
            task = task
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly,
        TaskCreateEditScreenUIState(isEditingEnabled = canWrite, task = TaskCreateEditScreenUIState.TaskUiState())
    )

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect(){
                userId = it
            }
        }

    }

    fun setAgendaTaskTypeCode(code: String){
        viewModelScope.launch {
            agendaTaskRepository.getActivityTypeTypes(taskType.name).firstOrNull { it.code == code }?.let {
                taskActivityType.value = it
            }
        }
    }

    fun setAgendaTaskTypeId(id: Int){
        viewModelScope.launch {
            agendaTaskRepository.getActivityTypeTypes(taskType.name).firstOrNull { it.id == id }?.let {
                duration.value = it.duration
                taskActivityType.value = it
            }
        }
    }

    fun setDate(value: Date){
        dateTime.value = value
    }

    fun setDuration(value: Int){
        duration.value = value
    }

    fun setNotes(value: String){
        notes.value = value
    }

    fun setGeneralNote(value: String){
        generalNote.value = value
    }

    fun setShowInDiary(value: Boolean){
        showInDiary.value = value
    }

    fun setNotExecuted(value: Boolean){
        notExecuted.value = value
        if (value && taskType == ToolTag.BloodExamTask) showInDiary.value = true
    }

    fun clearError(){
        errorMessage.value = null
    }

    fun clearData(){
        errorMessage.value = null
        isLoading.value = false
        showInDiary.value = false
        isSuccess.value = false
        notExecuted.value = false
        notes.value = ""
        duration.value = null
    }

    fun saveTask(task: AgendaTask?){
        task?.let {
            putTask(task)
        } ?: run {
            creteTask()
        }
    }


    private fun creteTask() {
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val newTask = UnscheduledTask(
                index = 1,
                taskType = taskType.name,
                taskFields = UnscheduledTaskFields(
                    codCase = caseCode,
                    idType = taskActivityType.value?.id ?: 0,
                    idUser = userId,
                    showInDiary = showInDiary.value,
                    duration = duration.value ?: 0,
                    notes = notes.value,
                    excDateTime = dateTime.value.format("yyyy.MM.dd HH:mm"),
                    skipped = notExecuted.value
                )
            )
            val res = agendaTaskRepository.addUnscheduledVitalSign(newTask)
            if(res.status == "success"){
                isSuccess.value = true
            } else if (res.error?.desc != null){
                errorMessage.value = res.error?.desc
            }
            isLoading.value = false

        }
    }

    private fun putTask(task: AgendaTask) {
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            if (task.expDate.isNullOrEmpty()){
                val newTask = UnscheduledTask(
                    index = 1,
                    taskType = taskType.name,
                    taskFields = UnscheduledTaskFields(
                        codCase = caseCode,
                        idType = taskActivityType.value?.id ?: 0,
                        idUser = userId,
                        showInDiary = showInDiary.value,
                        duration = duration.value ?: 0,
                        notes = notes.value,
                        excDateTime = dateTime.value.format("yyyy.MM.dd HH:mm"),
                        skipped = notExecuted.value,
                        id = task.pkey
                    )
                )
                val res = agendaTaskRepository.updateUnscheduledVitalSign(newTask)
                if(res.status == "success"){
                    isSuccess.value = true
                } else if (res.error?.desc != null){
                    errorMessage.value = res.error?.desc
                }
                isLoading.value = false

            }else{
                val execDate = DateFormat.format("yyyy-MM-dd", dateTime.value).toString()
                val execTime = DateFormat.format("HH:mm:00.000", dateTime.value).toString()
                val newTask = task.copy(
                    showInDiary = showInDiary.value,
                    duration = duration.value ?: 0,
                    notes = notes.value,
                    typeCode = taskActivityType.value?.code ?: task.typeCode,
                    execDate = execDate,
                    execTime = execTime,
                    alertLevel = 1,
                    isSkipped = notExecuted.value
                )

                val res = agendaTaskRepository.updateAgendaTasks(newTask)
                res.error?.let {
                    errorMessage.value = it.desc
                } ?: run {
                    isSuccess.value = true
                }

                isLoading.value = false
            }
        }
    }
}