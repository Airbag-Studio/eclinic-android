package it.airbagstudio.ticare.pages.vitalParameters.charts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.ChartMapper
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.ChartUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VitalParamsChartsViewModel @Inject constructor(
    private val agendaTaskRepository: AgendaTaskRepository,
    private val userDetailRepository: UserDetailRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val vitalSignTypes = agendaTaskRepository.getVitalSignTypes()

    private val _case = userDetailRepository.getCurrentCase()
    val case = _case

    private val _uiState = MutableStateFlow<ChartUiState>(ChartUiState.Loading)
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = ChartUiState.Error(
            throwable.localizedMessage ?: "Errore nel caricamento dei grafici"
        )
    }

    init {
        downloadParams()
    }

    fun downloadParams() {
        _uiState.value = ChartUiState.Loading

        viewModelScope.launch(coroutineExceptionHandler) {
            vitalSignTypes.collect { vitalSignTypes ->
                val responseList = agendaTaskRepository.getVitalSignsCharts(patientCod).results

                // Get the first response from the list (typically there's only one)
                val response = responseList?.firstOrNull()

                val chartConfigs = ChartMapper.mapToChartConfigs(vitalSignTypes,response)

                _uiState.value = if (chartConfigs.isEmpty()) {
                    ChartUiState.Empty("Nessun dato disponibile per questo paziente")
                } else {
                    ChartUiState.Success(chartConfigs)
                }
            }

        }
    }

    fun clearError() {
        if (_uiState.value is ChartUiState.Error) {
            _uiState.value = ChartUiState.Empty()
        }
    }
}