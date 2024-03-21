package it.airbagstudio.ticare.pages.patientInfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
        ToolbarWithBackAndSync(title = "${viewModel.caseInfo?.surname ?: ""} ${viewModel.caseInfo?.name ?: ""}") {
            onBack()
        }
    }) { values ->
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(values)
            .padding(horizontal = 16.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = stringResource(id = R.string.patient_info),
                style = MaterialTheme.typography.headlineSmall
            )
            viewModel.caseInfo?.let { caseInfo ->
                PatientInfoCard(tile = stringResource(id = R.string.bithday), text = caseInfo.birthday)
                PatientInfoCard(tile = stringResource(id = R.string.address), address = "${caseInfo.address}, ${caseInfo.cap}, ${caseInfo.locality}")
                if (caseInfo.contacts.phoneNumbers.isNotEmpty()) {
                    PatientInfoCard(tile = "Telefono", phones = caseInfo.contacts.phoneNumbers.split(" | "))
                }
                caseInfo.internalMedics.forEach { internalMedic ->
                    if (internalMedic.phoneNumbers.isNotEmpty()) {
                        PatientInfoCard(
                            tile = internalMedic.label,
                            phones = internalMedic.phoneNumbers.split(" | " )
                        )
                    }
                }
                caseInfo.externalMedics.forEach { externalMedic ->
                    if (externalMedic.phoneNumbers.isNotEmpty()) {
                        PatientInfoCard(
                            tile = "${externalMedic.label}\n${externalMedic.operator}",
                            phones = externalMedic.phoneNumbers.split(" | " )
                        )
                    }
                }
                caseInfo.otherInfo.forEach { otherInfo ->
                    PatientInfoCard(tile = otherInfo.name, text = otherInfo.value)
                }
                caseInfo.contacts.otherContacts.forEach {
                    PatientInfoCard(
                        tile = "${it.relationship} ${it.fullname}",
                        phones = it.phoneNumbers.split(" | " )
                    )
                }
            }

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