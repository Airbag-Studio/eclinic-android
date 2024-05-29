package it.airbagstudio.ticare.pages.patientsList.syncDataSheet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.SyncDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientsList.PatientListUiState
import it.airbagstudio.ticare.utils.getCategoryName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SyncDataSheetViewModelUIState(
    val syncDataError: List<SyncDataError> = listOf(),
    val progress: Float,
    val isSyncInProgress: Boolean = true,
    val isCompleted: Boolean = false
){
    data class SyncDataError(
        val patientCode: String,
        val birthDate: String,
        val completeName: String,
        val errors: List<PatientError>
    ){
        data class PatientError(
            val typeStringId: Int,
            val title: String,
            val time: String
        )
    }
}

@HiltViewModel
class SyncDataSheetViewModel @Inject constructor(
    private val syncDataRepository: SyncDataRepository?
) : ViewModel() {

    var syncNetworkError by mutableStateOf<String?>(null)

    lateinit var caseList: List<PatientListUiState.PatientUIState>
    private val isSyncInProgress = MutableStateFlow(true)
    private val isCompleted = MutableStateFlow(false)
    private val progress = MutableStateFlow(0f)
    private val errors = MutableStateFlow<List<SyncDataSheetViewModelUIState.SyncDataError>>(
        listOf()
    )

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        syncNetworkError = throwable.localizedMessage
    }

    val uiState = combine(
        isSyncInProgress,
        isCompleted,
        progress,
        errors
    ) { isSyncInProgress, isCompleted, progress, errors ->
        SyncDataSheetViewModelUIState(
            errors,
            progress,
            isSyncInProgress,
            isCompleted
        )
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly, SyncDataSheetViewModelUIState(
            listOf(), 0f, true,
            isCompleted = false
        )
    )

    fun startUpload() {
        viewModelScope.launch(coroutineExceptionHandler) {
            syncDataRepository?.syncOfflineCreatedData()?.collectLatest { syncDataState ->
                val _progress = (syncDataState.uploaded.toFloat() + syncDataState.failed.count()
                    .toFloat()) / syncDataState.total.toFloat()
                progress.value = _progress
                if (_progress == 1f) {
                    isSyncInProgress.value = false
                    isCompleted.value = true
                    val groupedErrors = syncDataState.failed.groupBy { it.code }
                    val _errors: MutableList<SyncDataSheetViewModelUIState.SyncDataError> = mutableListOf()
                    groupedErrors.keys.forEach { caseCode ->
                        val case = caseList.firstOrNull { it.patientCode == caseCode } ?: return@forEach
                        _errors.add(SyncDataSheetViewModelUIState.SyncDataError(
                            caseCode,
                            case.birthDate,
                            case.completeName,
                            groupedErrors.get(caseCode)?.map { SyncDataSheetViewModelUIState.SyncDataError.PatientError(
                                it.type.getCategoryName(),
                                it.name,
                                it.date
                            ) } ?: listOf()
                        ))
                    }
                    errors.value = _errors
                }

            }
        }

    }

    fun resetData() {
        isSyncInProgress.value = true
        isCompleted.value = false
        progress.value = 0f
        errors.value = mutableListOf()
        syncNetworkError = null
    }

}