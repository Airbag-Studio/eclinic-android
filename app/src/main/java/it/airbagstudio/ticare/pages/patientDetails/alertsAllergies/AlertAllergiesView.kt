package it.airbagstudio.ticare.pages.patientDetails.alertsAllergies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.patientDetails.AlertChip
import it.airbagstudio.ticare.pages.patientDetails.AllergyChip
import it.airbagstudio.ticare.ui.components.BuildPageHeader
import it.airbagstudio.ticare.ui.components.ToolbarWithBack

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlertAllergiesScreen(
    viewModel: AlertAllergiesViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            ToolbarWithBack(title = uiState.patientName) {
                onBack()
            }
        }
    ) {
        Column(Modifier.padding(it)) {
            BuildPageHeader(title = stringResource(id = R.string.alert_allergies), date = uiState.date, shiftName = uiState.shift)
            Column(modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())) {
                if (uiState.alerts.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.alerts),
                        style = MaterialTheme.typography.labelLarge
                    )
                    FlowRow(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        uiState.alerts.forEach { alert ->
                            AlertChip(alert, maxLines = 10){}
                        }
                    }
                }
                if (uiState.allergies.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.allergies),
                        style = MaterialTheme.typography.labelLarge
                    )
                    FlowRow(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        uiState.allergies.forEach { item ->
                            AllergyChip(item, maxLines = 10)
                        }
                    }
                }
            }

        }
    }
}