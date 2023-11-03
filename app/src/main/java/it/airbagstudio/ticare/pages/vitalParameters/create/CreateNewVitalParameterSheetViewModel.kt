package it.airbagstudio.ticare.pages.vitalParameters.create

import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.entity.UnscheduledTask
import ch.ticare.eclinic.library.entity.UnscheduledTaskFields
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


data class NewVitalParameterUIState(
    val date: Date,
    val notes: String,
    val duration: Int,
    val value: String,
    val showInDiary: Boolean,
    val mUSymbol: String,
    val description: String,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

private data class ItemValues(
    val date: Date,
    val notes: String,
    val duration: Int,
    val value: String,
    val showInDiary: Boolean,
)

@HiltViewModel
class CreateNewVitalParameterSheetViewModel @Inject constructor(
    private val agendaTaskRepository: AgendaTaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    var agendaTask: AgendaTask? = null
    var caseCode: String? = null
    var vitalSingId: Int? = null

    private var userId: Int? = null
    private val isLoading = MutableStateFlow<Boolean>(false)
    private val isSuccess = MutableStateFlow<Boolean>(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val vitalSignCode = MutableStateFlow<String?>(null)
    private val vitalSignTypes = agendaTaskRepository.getVitalSignTypes()
    private val vitalSignType = combine(vitalSignTypes, vitalSignCode) { types, code ->
        val type = types.firstOrNull { it.code == code }
        vitalSingId = type?.id
        type
    }

    private val date = MutableStateFlow<Date>(Date())
    private val notes = MutableStateFlow<String>("")
    private val duration = MutableStateFlow<Int>(0)
    private val value = MutableStateFlow<String>("")
    private val showInDiary = MutableStateFlow<Boolean>(true)

    private val itemValues = combine(
        date,
        notes,
        duration,
        value,
        showInDiary
    ) { date, notes, duration, value, showInDiary ->
        ItemValues(date, notes, duration, value, showInDiary)

    }

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            errorMessage.value = throwable.localizedMessage
            isLoading.value = false
        }


    val uiState = combine(
        itemValues,
        vitalSignType,
        errorMessage,
        isLoading,
        isSuccess
    ) { itemValues, vitalSignType, errorMessage, isLoading, isSuccess ->
        NewVitalParameterUIState(
            itemValues.date,
            itemValues.notes,
            itemValues.duration,
            itemValues.value,
            itemValues.showInDiary,
            vitalSignType?.muSymbol ?: "",
            vitalSignType?.desc ?: "",
            isLoading,
            errorMessage,
            isSuccess
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewVitalParameterUIState(
            Date(),
            "",
            0,
            "",
            true,
            "",
            ""
        )
    )

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect(){
                userId = it
            }
        }

    }

    fun setVitalSignCode(code: String) {
        vitalSignCode.value = code
    }

    fun setDate(date: Date) {
        this.date.value = date
    }

    fun setValue(value: String) {
        this.value.value = value
    }

    fun setShowInDiary(value: Boolean) {
        this.showInDiary.value = value
    }

    fun setNotes(value: String) {
        this.notes.value = value
    }

    fun setDuration(value: Int) {
        this.duration.value = value
    }

    fun saveTask() {
        agendaTask?.let { task ->
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
                taskType = AgendaTaskRepository.VITAL_SIGN_TYPE,
                taskFields = UnscheduledTaskFields(
                    codCase = caseCode ?: "",
                    idType = vitalSingId ?: 0,
                    idUser = userId ?: 0,
                    showInDiary = showInDiary.value,
                    duration = duration.value,
                    value = value.value,
                    notes = notes.value,
                    excDateTime = date.value.format("yyyy.MM.dd HH:mm"),
                    skipped = false
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
                    taskType = AgendaTaskRepository.VITAL_SIGN_TYPE,
                    taskFields = UnscheduledTaskFields(
                        codCase = caseCode ?: "",
                        idType = vitalSingId ?: 0,
                        idUser = userId ?: 0,
                        showInDiary = showInDiary.value,
                        duration = duration.value,
                        value = value.value,
                        notes = notes.value,
                        excDateTime = date.value.format("yyyy.MM.dd HH:mm"),
                        skipped = false,
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
                val execDate = DateFormat.format("yyyy-MM-dd", date.value).toString()
                val execTime = DateFormat.format("HH:mm:00.000", date.value).toString()
                val newTask = task.copy(
                    showInDiary = showInDiary.value,
                    duration = duration.value,
                    notes = notes.value,
                    typeCode = vitalSignCode.value ?: task.typeCode,
                    execDate = execDate,
                    execTime = execTime,
                    alertLevel = 1,
                    value = value.value
                )
                val res = agendaTaskRepository.updateAgendaTasks(listOf(newTask))
                res.error?.let {
                    errorMessage.value = it.desc
                } ?: run {
                    isSuccess.value = true
                }
                isLoading.value = false
            }
        }
    }

    fun clearError() {
        errorMessage.value = null
    }

    fun clearData() {
        errorMessage.value = null
        isSuccess.value = false
        duration.value = 0
        value.value = ""
        date.value = Date()
        notes.value = ""
    }

}