package it.airbagstudio.ticare.pages.vitalParameters.charts

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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

    // Get activity for orientation control
    val context = LocalContext.current
    val activity = context as? Activity

    // Manage screen orientation based on selected tab
    LaunchedEffect(selectedTabIndex) {
        activity?.requestedOrientation = if (selectedTabIndex == 1) {
            // Tabella: allow rotation
            ActivityInfo.SCREEN_ORIENTATION_SENSOR
        } else {
            // Grafici: force portrait
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    // Restore portrait orientation when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    // Detect orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Show topbar only if NOT in landscape table view
    val showTopBar = !(isLandscape && selectedTabIndex == 1)

    Scaffold(
        topBar = {
            if (showTopBar) {
                ToolbarWithBackAndSync(title = viewModel.case?.getCompleteName() ?: "") {
                    onBack()
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = if (showTopBar) {
                Modifier.padding(paddingValues)
            } else {
                Modifier.fillMaxSize()
            }
        ) {
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