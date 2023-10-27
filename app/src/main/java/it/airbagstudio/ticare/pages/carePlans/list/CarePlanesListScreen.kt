package it.airbagstudio.ticare.pages.carePlans.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync

@Composable
fun CarePlanesListScreen(
    viewModel: CarePlanesListScreenViewModel = hiltViewModel(),
            onBack: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = uiState.patientName) {
                onBack()
            }
        }
    ) { values ->

        Column(
            Modifier
                .padding(values)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(id = R.string.care_planes),
                style = MaterialTheme.typography.headlineSmall

            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isLoading) {
                repeat(8) {
                    PatientListItemViewLoading()
                }
            } else {

            }
        }
    }
}