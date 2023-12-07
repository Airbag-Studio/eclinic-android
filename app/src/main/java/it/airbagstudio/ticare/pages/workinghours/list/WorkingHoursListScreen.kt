package it.airbagstudio.ticare.pages.workinghours.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerViewModel
import it.airbagstudio.ticare.ui.theme.buttonDisableBg

@Composable
fun WorkingHoursListScreen(
    trackingViewModel: TimeTrackerViewModel = hiltViewModel(LocalActivity.current),
    onBack: () -> Unit
){

    val trackingUiState by trackingViewModel.uiState.collectAsStateWithLifecycle()

    var showSearchDialog by remember {
        mutableStateOf(false)
    }
    var showTrackingAlert by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        if (!trackingUiState.isEnabled){
            showTrackingAlert = true
        }
    }
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = stringResource(id = R.string.consumption_title)) {
                onBack()
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                containerColor = if (trackingUiState.isEnabled) MaterialTheme.colorScheme.primaryContainer else buttonDisableBg,
                contentColor = if (trackingUiState.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                content = {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = stringResource(
                            id = R.string.new_treatment
                        )
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(
                            id = R.string.new_treatment
                        )
                    )
                },
                onClick = {
                    if (trackingUiState.isEnabled) {
                        showSearchDialog = true
                    }else{
                        showTrackingAlert = true
                    }
                })
        }
    ) {
        Column(Modifier.padding(it)) {

        }
    }
    if (showTrackingAlert){
        StartTrackerForWorkingHoursDialog(onDismissRequest = {confirm ->
            showTrackingAlert = false
            if (confirm){
                trackingViewModel.startTracker {

                }
            }
        })
    }
}


@Composable
private fun StartTrackerForWorkingHoursDialog(
    onDismissRequest: (Boolean) -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.working_hours_tracking_alert_title)) },
        text = { Text(text = stringResource(id = R.string.working_hours_tracking_alert_text)) },
        onDismissRequest = {
            onDismissRequest(false)
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest(true)
            }) {
                Text(text = stringResource(id = R.string.working_hours_tracking_alert_confirm_button))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest(false)
            }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )


}