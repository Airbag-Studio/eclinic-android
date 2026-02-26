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
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.DateFilterOption
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.VitalSignChartConfig
import it.airbagstudio.ticare.utils.toDate
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

    // Date filter state
    private val _selectedFilter = MutableStateFlow(DateFilterOption.ALL)
    val selectedFilter: StateFlow<DateFilterOption> = _selectedFilter.asStateFlow()

    // Store original data for filtering
    private var allChartConfigs: List<VitalSignChartConfig> = emptyList()
    private var fromDate: String? = null
    private var toDate: String? = null

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

                // Store date range from response
                fromDate = response?.from?.substringBefore(" ")
                toDate = response?.to?.substringBefore(" ")

                val chartConfigs = ChartMapper.mapToChartConfigs(vitalSignTypes, response)

                // Store original data for filtering
                allChartConfigs = chartConfigs

                _uiState.value = if (chartConfigs.isEmpty()) {
                    ChartUiState.Empty("Nessun dato disponibile per questo paziente")
                } else {
                    ChartUiState.Success(ChartMapper.filterByDate(chartConfigs, _selectedFilter.value))
                }
            }

        }
    }

    fun setFilter(filter: DateFilterOption) {
        _selectedFilter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        if (allChartConfigs.isEmpty()) return

        val filteredConfigs = ChartMapper.filterByDate(allChartConfigs, _selectedFilter.value)
        _uiState.value = if (filteredConfigs.isEmpty()) {
            ChartUiState.Empty("Nessun dato disponibile per il periodo selezionato")
        } else {
            ChartUiState.Success(filteredConfigs)
        }
    }

    fun getDateRangeLabel(): String {
        return "Dal ${fromDate ?: ""} al ${toDate ?: ""}"
    }

    fun clearError() {
        if (_uiState.value is ChartUiState.Error) {
            _uiState.value = ChartUiState.Empty()
        }
    }
}