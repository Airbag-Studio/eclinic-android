package it.airbagstudio.ticare.pages.patientDetails.alertsAllergies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.CaseAllergiesRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.pages.patientDetails.AllergiesItem
import it.airbagstudio.ticare.utils.getCompleteName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertAllergiesUiState(
    val patientName: String,
    val date: String,
    val shift: String,
    val alerts: List<AlertItem> = listOf(),
    val allergies: List<AllergiesItem> = listOf()
)
@HiltViewModel
class AlertAllergiesViewModel @Inject constructor(
    val caseAllergiesRepository: CaseAllergiesRepository,
    val userDetailRepository: UserDetailRepository,
    val savedStateHandle: SavedStateHandle
): ViewModel() {

    val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]


    private var _uiState = MutableStateFlow(AlertAllergiesUiState("","",""))
    val uiState: StateFlow<AlertAllergiesUiState> = _uiState

    init {
        viewModelScope.launch {
            patientCod?.let { caseCode ->
                val date = userDetailRepository.getSelectedDate()
                val shift = userDetailRepository.getCurrentShift()
                val patientInfo = userDetailRepository.getCurrentCase()
                val allergies = caseAllergiesRepository.getCaseAllergies(caseCode).results?.map { AllergiesItem(it.desc,it.isDrug) } ?: listOf()
                val alerts = userDetailRepository.getCaseAlerts(caseCode).results?.map { AlertItem(
                    colorFg = it.foreground,
                    colorBg = it.background,
                    label = it.label
                ) } ?: listOf()
                _uiState.value = AlertAllergiesUiState(
                    patientName = patientInfo?.getCompleteName() ?: "",
                    date = date ?: "",
                    shift = shift?.name ?: "Tutti",
                    alerts = alerts,
                    allergies = allergies
                )

            }
        }
    }


}