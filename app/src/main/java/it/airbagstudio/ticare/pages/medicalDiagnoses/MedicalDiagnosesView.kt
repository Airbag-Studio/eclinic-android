package it.airbagstudio.ticare.pages.medicalDiagnoses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ToolbarWithBack
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.getLabelId

@Composable
fun MedicalDiagnosesView(
    viewModel: MedicalDiagnosesViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.downloadData()
            }
            else -> {}
        }
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            ToolbarWithBack(title = "Patient name") {
                onBack()
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Text(
                text = stringResource(id = R.string.medical_diagnoses),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${uiState?.date ?: ""} - ${uiState?.shift ?: stringResource(id = R.string.all)}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            LazyColumn() {
                items(uiState?.medicalDiagnoses ?: listOf<MedicalDiagnosesListItem>()){
                    MedicalDiagnosesListItemView(it) {

                    }
                }

            }


        }
    }
}

@Preview
@Composable
fun MedicalDiagnosesViewPreview() {
    AppTheme() {
        MedicalDiagnosesView(){}
    }
}