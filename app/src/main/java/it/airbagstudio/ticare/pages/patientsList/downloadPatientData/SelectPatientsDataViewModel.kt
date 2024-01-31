package it.airbagstudio.ticare.pages.patientsList.downloadPatientData

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.getCompleteName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


data class SelectPatientsDataViewModelUIState(
    var query: String,
    var isDownloading: Boolean,
    var progress: Float,
    var patients: List<PatientUiState>

) {
    data class PatientUiState(
        var patientCode: String,
        var name: String,
        var isSelected: Boolean
    )
}

@HiltViewModel
class SelectPatientsDataViewModel @Inject constructor() : ViewModel() {

    var errorMessage by mutableStateOf<String?>(null)
    private var progress = MutableStateFlow(0.0f)
    private var query = MutableStateFlow("")
    private var isDownloading = MutableStateFlow(false)
    private var selectedPatients = MutableStateFlow(listOf<String>())
    private var patients = MutableStateFlow<List<CaseInfo>>(listOf())

    var uiState = combine(
        patients,
        selectedPatients,
        isDownloading,
        query,
        progress
    ) { patients, selectedPatients, isDownloading, query, progress ->
        val cases = patients.filter { it.getCompleteName().contains(query, true) }.map {
            SelectPatientsDataViewModelUIState.PatientUiState(
                patientCode = it.code,
                name = it.getCompleteName(),
                isSelected = selectedPatients.contains(it.code)
            )
        }
        SelectPatientsDataViewModelUIState(
            query = query,
            isDownloading = isDownloading,
            progress = progress,
            patients = cases
        )

    }.catch {
        errorMessage = it.localizedMessage
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = SelectPatientsDataViewModelUIState(
            query = "",
            isDownloading = false,
            progress = 0f,
            patients = listOf()
        )
    )

    fun setCases(value: List<CaseInfo>) {
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
    }
}