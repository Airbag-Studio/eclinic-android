package it.airbagstudio.ticare.pages.patientsList.downloadPatientData

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseInfo
import ch.ticare.eclinic.library.repository.SyncDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientsList.PatientListUiState
import it.airbagstudio.ticare.utils.getCompleteName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SelectPatientsDataViewModelUIState(
    var query: String,
    var syncState: SyncState,
    var patients: List<PatientUiState>

) {
    data class SyncState(
        var isDownloading: Boolean = false,
        var progress: Float = 0f,
        var isCompleted: Boolean = false
    )

    data class PatientUiState(
        var patientCode: String,
        var name: String,
        var isSelected: Boolean
    )
}

@HiltViewModel
class SelectPatientsDataViewModel @Inject constructor(
    private val syncDataRepository: SyncDataRepository
) : ViewModel() {

    var errorMessage by mutableStateOf<String?>(null)
    private var progress = MutableStateFlow(0.0f)
    private val isCompleted = MutableStateFlow(false)
    private var query = MutableStateFlow("")
    private var isDownloading = MutableStateFlow(false)
    private var selectedPatients = MutableStateFlow(listOf<String>())
    private var patients = MutableStateFlow<List<PatientListUiState.PatientUIState>>(listOf())

    private var coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        errorMessage = throwable.localizedMessage
    }

    private val syncState = combine(isCompleted,isDownloading,progress){ isCompleted,isDownloading,progress ->
        SelectPatientsDataViewModelUIState.SyncState(
            isDownloading = isDownloading,
            isCompleted = isCompleted,
            progress = progress
        )
    }

    var uiState = combine(
        patients,
        selectedPatients,
        syncState,
        query
    ) { patients, selectedPatients, syncState, query ->
        val cases = patients.filter { it.completeName.contains(query, true) }.map {
            SelectPatientsDataViewModelUIState.PatientUiState(
                patientCode = it.patientCode,
                name = it.completeName,
                isSelected = selectedPatients.contains(it.patientCode)
            )
        }
        SelectPatientsDataViewModelUIState(
            query = query,
            syncState = syncState,
            patients = cases
        )

    }.catch {
        errorMessage = it.localizedMessage
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = SelectPatientsDataViewModelUIState(
            query = "",
            syncState = SelectPatientsDataViewModelUIState.SyncState(),
            patients = listOf()
        )
    )

    fun setCases(value: List<PatientListUiState.PatientUIState>) {
        patients.value = value
    }

    fun setQuery(value: String) {
        query.value = value
    }

    fun togglePatientSelection(patientCode: String) {
        val list = selectedPatients.value.toMutableList()
        if (list.contains(patientCode)) {
            list.remove(patientCode)
        } else {
            list.add(patientCode)
        }
        selectedPatients.value = list.toList()
    }

    fun startDownloadPatientData() {
        isDownloading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            syncDataRepository.downloadPatientOffline(selectedPatients.value).collectLatest {
                progress.value = it.toFloat()/selectedPatients.value.count().toFloat()
                if (progress.value == 1f){
                    isCompleted.value = true
                }
            }
        }

    }

    fun resetData(){
        progress.value = 0f
        isCompleted.value = false
        isDownloading.value = false
    }
}