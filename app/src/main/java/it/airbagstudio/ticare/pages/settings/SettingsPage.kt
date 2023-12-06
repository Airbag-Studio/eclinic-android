package it.airbagstudio.ticare.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.ConfirmWithNoteDialog
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBack
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerViewModel
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun SettingsPage(
    timeTrackerViewModel: TimeTrackerViewModel = hiltViewModel(LocalActivity.current),
    viewModel: SettingsPageViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
) {
    var showNotesDialog by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            ToolbarWithBack(title = stringResource(id = R.string.setting)) {
                onBack()
            }
        }
    ) {

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val trackerUiState by timeTrackerViewModel.uiState.collectAsStateWithLifecycle()
        var showTrackingEnabledDialog by remember {
            mutableStateOf(false)
        }
        Column(Modifier.padding(it)) {
            Divider()
            SettingsListItem(title = stringResource(id = R.string.consumption_title), subtitle = stringResource(
                id = R.string.consumption_subtitle
            )) {
                navigationActions.navigateToConsumptionList()
            }
            Divider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.show_all_patient),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    enabled = uiState.canRequestAllCases,
                    checked = uiState.isAllCaseActive,
                    onCheckedChange = { selected ->
                        if (selected) {
                            showNotesDialog = true
                        } else {
                            viewModel.clearAllCasesRequest()
                        }

                    })
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                enabled = !uiState.isDoingLogout,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                onClick = {
                    if (trackerUiState.isEnabled) {
                        showTrackingEnabledDialog = true
                    }else{
                        viewModel.requestLogout()
                    }
                }) {
                Text(text = stringResource(id = R.string.logout))
                if (uiState.isDoingLogout) {
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }


        }
        if (showNotesDialog) {
            ConfirmWithNoteDialog(
                title = stringResource(id = R.string.enable_all_patients_dialog_title),
                body = stringResource(id = R.string.enable_all_patients_dialog_body),
                onDismissRequest = { confirm, notes ->
                    if (confirm && !notes.isNullOrEmpty()) {
                        viewModel.requestAllCasesAccess(notes)
                    }
                    showNotesDialog = false
                })
        }
        if (uiState.isLoggedOut && !trackerUiState.isEnabled) {
            navigationActions.navigateToLogin()
        }
        if (showTrackingEnabledDialog) {
            AlertDialog(
                title = {
                    Text(text = stringResource(id = R.string.time_tracking_enabled_dialog_title))
                },
                text = {
                    Text(text = stringResource(id = R.string.time_tracking_enabled_dialog_text))
                },
                onDismissRequest = {
                    showTrackingEnabledDialog = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        timeTrackerViewModel.stopTracker()
                        viewModel.requestLogout()
                    }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showTrackingEnabledDialog = false
                    }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }

            )
        }
        if (viewModel.errorMessage != null){
            ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
                viewModel.errorMessage = null
            })
        }
    }
}

@Composable
private fun SettingsListItem(title: String,subtitle:String,onClick: () -> Unit){
    Row(
        modifier = Modifier.clickable {
            onClick()
        }.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
    }
}

@Composable
@Preview
private fun PreviewSettingsListItem(){
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                SettingsListItem("Consumi e rimborsi","Registrazione note spese"){

                }
                Divider(modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}