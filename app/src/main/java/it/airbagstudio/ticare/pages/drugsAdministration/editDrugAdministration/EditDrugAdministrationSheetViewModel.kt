package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.utils.validated
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditDrugAdministrationSheetViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val agendaTaskRepository: AgendaTaskRepository
) : ViewModel() {

    var task = mutableStateOf<AgendaTask?>(null)

    var quantity = mutableStateOf<String?>("")

    var isLoading by mutableStateOf(false)
    var isSucces by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var messagesStringIdentifiers by mutableStateOf<List<Int>?>(null)

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    var isEditingEnable = {
        task.value?.validated() ?: false
    }

    fun setDuration(value: Int?){
        task.value = task.value?.copy(duration = value ?: 0)
    }

    fun setQuantity(value: String){
        quantity.value = value
        val newValue = value.toDoubleOrNull() ?: 0.0
        task.value = task.value?.copy(quantity = newValue)
        if (newValue < (task.value?.maxQuantity ?: 0.0)){
            task.value = task.value?.copy(showInDiary = true)
        }
    }

    fun setExecutedDate(date: Date){
        val execDate = DateFormat.format("yyyy-MM-dd", date).toString()
        val execTime = DateFormat.format("HH:mm:ss.000", date).toString()
        task.value = task.value?.copy(execDate = execDate, execTime = execTime)
    }

    fun setShowInDiary(value: Boolean){
        task.value = task.value?.copy(showInDiary = value)
    }

    fun setRejected(value: Boolean){
        task.value = task.value?.copy(rejected = value,quantity = 0.0, showInDiary = true, isSkipped = true)
    }

    fun setNotExecuted(value: Boolean){
        task.value = task.value?.copy(isSkipped = value, quantity = 0.0, showInDiary = true)
    }

    fun setPatientDrug(value: Boolean){
        task.value = task.value?.copy(patientOwnedDrug = value)
    }

    fun executeTask() {
        task.value?.let { updatedTask ->
            if (updatedTask.quantity > updatedTask.maxQuantity){
                messagesStringIdentifiers = listOf(R.string.over_max_quantity_error)
                return
            }
            if (updatedTask.quantity < updatedTask.maxQuantity && updatedTask.notes.isEmpty() && !updatedTask.isReserve){
                messagesStringIdentifiers = listOf(R.string.notes_mandatory)
                return
            }
            if ((updatedTask.isSkipped || (updatedTask.rejected == true)) && updatedTask.notes.isEmpty()){
                messagesStringIdentifiers = listOf(R.string.notes_mandatory)
                return
            }
            viewModelScope.launch(coroutineExceptionHandler) {
                isLoading = true
                val res = agendaTaskRepository.updateAgendaTasks(listOf(updatedTask))
                res.error?.let {
                    errorMessage = it.desc
                } ?: run {
                    isSucces = true
                }
                isLoading = false
            }
        }

    }
}