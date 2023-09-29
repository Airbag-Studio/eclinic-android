package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class EditDrugAdministrationSheetViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository
) : ViewModel() {

    var task = mutableStateOf<AgendaPharmacologicalTask?>(null)

    var isLoading by mutableStateOf(false)
    var isSucces by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var isValid = {
        (task.value?.quantity ?: 0) > 0
    }

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    fun setDuration(value: Int?){
        task.value = task.value?.copy(duration = value ?: 0)
    }

    fun setQuantity(value: Int?){
        val newValue = min(value ?: 0,task.value?.expQuantity ?: 0)
        task.value = task.value?.copy(quantity = newValue)
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
        task.value = task.value?.copy(rejected = value)
    }

    fun setNotExecuted(value: Boolean){
        task.value = task.value?.copy(isSkipped = value)
    }

    fun setPatientDrug(value: Boolean){
        task.value = task.value?.copy(patientOwnedDrug = value)
    }

    fun executeTask() {
        task.value?.let { updatedTask ->
            viewModelScope.launch(coroutineExceptionHandler) {
                isLoading = true
                val res = userDetailRepository.updatePharmacologicalTask(updatedTask)
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