package it.airbagstudio.ticare.pages.workinghours.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import ch.ticare.eclinic.library.entity.WorkingHoursType
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.consumptions.create.ConsumptionCreateScreen
import it.airbagstudio.ticare.pages.workinghours.create.WorkingHoursItemCreate
import it.airbagstudio.ticare.pages.workinghours.search.WorkingHoursSearch
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.components.lists.TitleDateListItem
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerViewModel
import it.airbagstudio.ticare.ui.theme.buttonDisableBg
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate

@Composable
fun WorkingHoursListScreen(
    trackingViewModel: TimeTrackerViewModel = hiltViewModel(LocalActivity.current),
    viewModel: WorkingHoursListScreenViewModel = hiltViewModel(),
    onBack: () -> Unit
){

    val trackingUiState by trackingViewModel.uiState.collectAsStateWithLifecycle()

    var showSearchDialog by remember {
        mutableStateOf(false)
    }
    var showTrackingAlert by remember {
        mutableStateOf(false)
    }
    var showCreateDialog by remember {
        mutableStateOf(false)
    }
    var selectedWorkingHoursType by remember {
        mutableStateOf<WorkingHoursType?>(null)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            if (uiState.isLoading) {
                repeat(8) {
                    PatientListItemViewLoading()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 124.dp),
                    content = {
                        items(uiState.workingHours.keys.toList()) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.titleMedium,
                                text = it
                            )
                            val items = uiState.workingHours.get(it)

                            items?.forEach { item ->
                                TitleDateListItem(
                                    name = item.type,
                                    date = item.date.toDate("dd.MM.yyyy")?.format("dd/MM/yyyy") ?: ""
                                ) {
                                    viewModel.setSelectedConsumption(item.id)
                                    showCreateDialog = true
                                }
                                Divider(modifier = Modifier.padding(start = if (items.lastOrNull() == item) 0.dp else 16.dp))
                            }
                        }
                    })
            }
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
    if (showCreateDialog){
        WorkingHoursItemCreate(
            typeId = selectedWorkingHoursType?.id,
            employeeWorkingHour = viewModel.selectedWorkingHour,
        ){
            showCreateDialog = false
            viewModel.setSelectedConsumption(null)
            selectedWorkingHoursType = null
            if(it){
                viewModel.downloadData()
            }
        }
    }
    if(showSearchDialog){
        WorkingHoursSearch(onDismissRequest = {
            showSearchDialog = false
            selectedWorkingHoursType = it
            if (selectedWorkingHoursType != null){
                showCreateDialog = true
            }
        })
    }
    if (uiState.errorMessage != null){
        ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
            viewModel.clearError()
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