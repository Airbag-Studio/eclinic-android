package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

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
import javax.inject.Inject

@HiltViewModel
class EditDrugAdministrationSheetViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository
) : ViewModel() {

    var task = mutableStateOf<AgendaPharmacologicalTask?>(null)

    var isLoading by mutableStateOf(false)
    var isSucces by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    fun setDuration(value: Int?){
        task.value?.duration = value ?: 0
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