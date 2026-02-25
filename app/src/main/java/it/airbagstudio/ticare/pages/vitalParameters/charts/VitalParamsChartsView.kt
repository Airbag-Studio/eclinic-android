package it.airbagstudio.ticare.pages.vitalParameters.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.ChartUiState
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.TableMapper
import it.airbagstudio.ticare.pages.vitalParameters.charts.ui.VitalSignChart
import it.airbagstudio.ticare.pages.vitalParameters.charts.ui.VitalSignsTable
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.utils.getCompleteName

@Composable
fun VitalParamsChartsView(
    viewModel: VitalParamsChartsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Grafici", "Tabella")

    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = viewModel.case?.getCompleteName() ?: "") {
                onBack()
            }
        },
    ) {
        Column(modifier = Modifier.padding(it))  {
            when (uiState) {
                is ChartUiState.Loading -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ChartUiState.Success -> {
                    val charts = (uiState as ChartUiState.Success).charts

                    // Tab Row
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    // Tab Content
                    when (selectedTabIndex) {
                        0 -> {
                            // Tab Grafici (existing charts view)
                            LazyColumn {
                                items(charts.size) { index ->
                                    val config = charts[index]
                                    VitalSignChart(config = config)
                                }
                            }
                        }
                        1 -> {
                            // Tab Tabella (new table view)
                            val tableData = TableMapper.mapToTableData(charts)
                            VitalSignsTable(
                                tableData = tableData,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                is ChartUiState.Empty -> {
                    Text((uiState as ChartUiState.Empty).message)
                }
                is ChartUiState.Error -> {
                    Text((uiState as ChartUiState.Error).message)
                }
            }
        }
    }
}