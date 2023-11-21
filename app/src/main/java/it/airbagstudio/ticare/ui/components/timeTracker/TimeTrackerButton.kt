package it.airbagstudio.ticare.ui.components.timeTracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.redColor
import it.airbagstudio.ticare.ui.theme.time_tracker_green

@Composable
fun TimeTrackerButton(
    viewModel: TimeTrackerViewModel = hiltViewModel(LocalActivity.current)
) {

    var expanded by remember { mutableStateOf(false) }
    var showStopTrackerDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(
            onClick = {
                expanded = !expanded
                if (expanded){
                    viewModel.updateElapsedTime()
                }
            }) {
            val painter = if (uiState.isEnabled) {
                painterResource(id = R.drawable.ic_time_tracker_enabled)
            } else {
                painterResource(id = R.drawable.ic_time_tracker_disabled)
            }
            Image(painter = painter, contentDescription = "")
        }
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {

            DropdownMenu(
                expanded = expanded,
                modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)
                    .background(Color.White),
                onDismissRequest = { expanded = false },
            ) {
                TimeTrackerPanel(uiState = uiState) {
                    if (uiState.isEnabled) {
                        showStopTrackerDialog = true
                    } else {
                        viewModel.startTracker(){}
                    }
                    expanded = false
                }

            }
        }
        if (viewModel.errorMessage != null){
            ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
                viewModel.errorMessage = null
            })
        }
    }
    if (showStopTrackerDialog){
        StopTrackerDialog(startTime = uiState.startTime, totalWorkingTime = uiState.elapsedTimeForDialog, onDismissRequest = { confirm ->
            if (confirm){
                viewModel.stopTracker()
            }
            showStopTrackerDialog = false
        })
    }
}

@Composable
private fun TimeTrackerPanel(uiState: TimeTrackerViewUIState, onButtonClick: () -> Unit) {
    Column() {
        Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            if (uiState.isEnabled) {
                Text(
                    text = stringResource(id = R.string.time_tracking_status),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.time_tracking_enabled),
                    style = MaterialTheme.typography.bodySmall,
                    color = time_tracker_green
                )
            } else {
                Text(
                    text = stringResource(id = R.string.time_tracking_disabled),
                    style = MaterialTheme.typography.titleMedium,
                    color = redColor
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.working_time),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                BasicTextField(
                    modifier = Modifier.width(160.dp),
                    enabled = uiState.isEnabled,
                    value = uiState.elapsedTime,
                    onValueChange = {},
                    textStyle = MaterialTheme.typography.headlineLarge
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.start_date_time),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = uiState.startTime,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Divider()
        Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isEnabled) redColor else time_tracker_green
                ),
                onClick = onButtonClick
            ) {
                Icon(
                    painter = if (uiState.isEnabled)
                        painterResource(id = R.drawable.ic_stop_time_tracker) else
                        painterResource(
                            id = R.drawable.ic_start_time_tracker
                        ), contentDescription = ""
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text(
                    text = if (uiState.isEnabled) stringResource(id = R.string.stop_work) else stringResource(
                        id = R.string.start_work
                    )
                )
            }
        }


    }
}


@Composable
@Preview
private fun TimeTrackerPanelPreviewON() {
    val uiState = TimeTrackerViewUIState(
        isEnabled = true,
        startTime = "22/11/2023 - 09:27",
        elapsedTime = "00:34",
        "",
        0
    )
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                TimeTrackerPanel(uiState = uiState) {}
            }
        }
    }
}

@Composable
@Preview
private fun TimeTrackerPanelPreviewOFF() {
    val uiState = TimeTrackerViewUIState(
        isEnabled = false,
        startTime = "--",
        elapsedTime = "00:00",
        "",
        0
    )
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                TimeTrackerPanel(uiState = uiState) {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun TimeTrackerButtonPreview() {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { "Test timer" },
                    actions = {
                        TimeTrackerButton()
                    }
                )
            }
        ) {
            Column(Modifier.padding(it)) {

            }
        }

    }
}