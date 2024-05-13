package it.airbagstudio.ticare.pages.workinghours.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.SaveEmployeeWorkingHour
import ch.ticare.eclinic.library.repository.UserMarkingRepository
import ch.ticare.eclinic.library.repository.UserRepository
import ch.ticare.eclinic.library.repository.WorkingHourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class WorkingHoursItemCreateUiState(
    val isSuccess: Boolean,
    val title: String,
    val errorMessage: String?,
    val isLoading: Boolean,
    val item: Item,
    val isValid: Boolean
) {
    data class Item(
        val date: Date,
        val duration: Int?,
        val notes: String,
        val editable: Boolean = false
    )
}

@HiltViewModel
class WorkingHoursItemCreateViewModel @Inject constructor(
    private val workingHourRepository: WorkingHourRepository,
    private val userMarkingRepository: UserMarkingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val isSuccess = MutableStateFlow<Boolean>(false)

    private val types = workingHourRepository.getTypes()
    private val workingHourId = MutableStateFlow<Int?>(null)
    private val selectedTypeId = MutableStateFlow<Int?>(null)
    private val date = MutableStateFlow<Date>(Date())
    private val duration = MutableStateFlow<String>("")
    private val notes = MutableStateFlow<String>("")
    private val clinicType = userRepository.getClinicType()

    private val isLoading = MutableStateFlow<Boolean>(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val timeFromLastActivity = userMarkingRepository.getMinutesFromLastActivity()

    private val selectedType = combine(types, selectedTypeId){ _types, _id ->
        _types.firstOrNull { it.id == _id }
    }

    private val workingHour =
        combine(date, duration, notes, clinicType) { _date, _duration, _notes, clinicType ->
            WorkingHoursItemCreateUiState.Item(_date,_duration.toIntOrNull(),_notes, clinicType == ClinicType.SPITEX)
        }

    val uiState = combine(workingHour, isLoading, errorMessage,selectedType,isSuccess) { workingHour, isLoading, errorMessage, selectedArticle, isSuccess ->
        val isValid = workingHour.duration != null
        WorkingHoursItemCreateUiState(
            title = selectedArticle?.name ?: "",
            errorMessage = errorMessage,
            isLoading = isLoading,
            item = workingHour,
            isSuccess = isSuccess,
            isValid = isValid
        )
    }.catch {
        errorMessage.value = it.localizedMessage
        isLoading.value = false
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = WorkingHoursItemCreateUiState(
            title = "",
            errorMessage = null,
            isLoading = false,
            item = WorkingHoursItemCreateUiState.Item(Date(), 0, ""),
            isSuccess = false,
            isValid = false
        )
    )

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        errorMessage.value = throwable.localizedMessage
        isLoading.value = false
    }

    fun setSelectedTypeId(id: Int?){
        this.selectedTypeId.value = id
    }

    fun setDate(date: Date){
        this.date.value = date
    }

    fun setDuration(value: String){
        this.duration.value = value
    }

    fun setNotes(value: String) {
        this.notes.value = value
    }

    fun clearErrors() {
        errorMessage.value = null
    }

    fun setId(id: Int?){
        workingHourId.value = id
        if (id == null) {
            viewModelScope.launch {
                timeFromLastActivity.mapNotNull { it }.collect {
                    setDuration(it.toString())
                }
            }
        }
    }

    fun clearData() {
        this.isSuccess.value = false
        this.selectedTypeId.value = null
        date.value = Date()
        duration.value = ""
        notes.value = ""
    }

    fun saveWorkingHour() {
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading.value = true
            val totalHours = "%02d:%02d".format((duration.value.toInt() / 60.0).toInt(), duration.value.toInt() % 60)
            val item = SaveEmployeeWorkingHour(
                id = workingHourId.value,
                idType = selectedTypeId.value!!,
                remarks = notes.value,
                totalHours = totalHours,
                date = date.value.format("yyyy.MM.dd")
            )
            if (item.id != null) {
                val res = workingHourRepository.editWorkingHour(item)
                res.error?.desc?.let {
                    errorMessage.value = it
                } ?: run{
                    isSuccess.value = true
                }
            } else {
                val res = workingHourRepository.addWorkingHour(item)
                res.error?.desc?.let {
                    errorMessage.value = it
                } ?: run{
                    isSuccess.value = true
                }
            }
            isLoading.value = false
        }
    }
}