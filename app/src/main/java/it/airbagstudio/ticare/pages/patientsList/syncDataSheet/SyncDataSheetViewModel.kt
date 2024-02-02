package it.airbagstudio.ticare.pages.patientsList.syncDataSheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.SyncDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SyncDataSheetViewModelUIState(
    val errors: Int,
    val progress: Float,
    val isSyncInProgress: Boolean = true,
    val isCompleted: Boolean = false
)

@HiltViewModel
class SyncDataSheetViewModel @Inject constructor(
    private val syncDataRepository: SyncDataRepository?
) : ViewModel() {

    private val isSyncInProgress = MutableStateFlow(true)
    private val isCompleted = MutableStateFlow(false)
    private val progress = MutableStateFlow(0f)
    private val errors = MutableStateFlow(0)

    val uiState = combine(isSyncInProgress,isCompleted,progress,errors){isSyncInProgress,isCompleted,progress,errors ->
        SyncDataSheetViewModelUIState(
            errors,
            progress,
            isSyncInProgress,
            isCompleted
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly,SyncDataSheetViewModelUIState(0,0f,true,
        isCompleted = false
    ))

    init {
        viewModelScope.launch {
            syncDataRepository?.syncOfflineCreatedData()?.collectLatest { syncDataState ->
                val _progress = (syncDataState.uploaded.toFloat() + syncDataState.failed.toFloat()) / syncDataState.total.toFloat()
                progress.value = _progress
                if (_progress == 1f){
                    isSyncInProgress.value = false
                    isCompleted.value = true
                    errors.value = syncDataState.failed
                }

            }
        }

    }

    fun resetData(){
        isSyncInProgress.value = true
        isCompleted.value = false
        progress.value = 0f
        errors.value = 0
    }

}