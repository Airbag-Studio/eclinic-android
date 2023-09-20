package it.airbagstudio.ticare.pages.patientInfo

import android.app.LocaleConfig
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun PatientInfoScreen(
    viewModel: PatientInfoScreenViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack:() -> Unit
){
    Scaffold(topBar = {
        ToolbarWithBackAndSync(title = "Patient Name") {
            onBack()
        }
    }) { values ->
        Column(modifier = Modifier
            .padding(values)
            .padding(horizontal = 16.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = stringResource(id = R.string.patient_info),
                style = MaterialTheme.typography.headlineSmall
            )
            PatientInfoCard(tile = stringResource(id = R.string.bithday), text = "28.12.1926 (97)")
            PatientInfoCard(tile = stringResource(id = R.string.address), address = "Via Calanchi 2, 6900 Lugano")
            PatientInfoCard(tile = "Telefono 1", phones = listOf("091 993 30 72"))
            PatientInfoCard(tile = "Dott.ssa Lyana Sorgesa", phones = listOf("091 993 30 72","091 923 75 61"))
        }
    }
}

@Composable
@Preview
private fun PreviewPatientInfoScreen(){
    AppTheme {
        PatientInfoScreen(navigationActions = NavigationActions(NavController(LocalContext.current))) {
            
        }
    }
}