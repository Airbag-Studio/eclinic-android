package it.airbagstudio.ticare.pages.carePlans.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.HomeCareActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.toDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditCareScreen(
    viewModel: CreateEditCareScreenViewModel = hiltViewModel(),
    homeCareActivity: HomeCareActivity? = null,
    codCase: String,
    plannedActivityId: Int? = null,
    carePlanId: Int? = null,
    idActivityType: Int? = null,
    onDismissRequest: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.clearData()
        viewModel.setPlannedActivityId(plannedActivityId)

        viewModel.setCodCase(codCase)
        viewModel.setCarePlanId(carePlanId)
        if (homeCareActivity != null){
            viewModel.setActivityId(homeCareActivity.id)
            viewModel.setPlannedActivityId(homeCareActivity.idScheduler)
            viewModel.setNotes(homeCareActivity.notes)
            viewModel.setDuration(homeCareActivity.duration)
            viewModel.setDate(homeCareActivity.execDateTime.toDate("dd.MM.yyyy HH:mm") ?: Date())
            viewModel.setShowInDiary(homeCareActivity.showInDiary)
        }else{
            viewModel.setIdActivityType(idActivityType)
        }
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAlertNotPlannedActivity by remember {
        mutableStateOf(false)
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismissRequest(false) },
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = uiState.title)
                        }

                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.clearData()
                            onDismissRequest(false)
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            }) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    CalendarTextField(
                        modifier = Modifier.weight(1f),
                        date = uiState.item.date, label = {
                            Text(text = stringResource(id = R.string.actual_date_time))
                        }, onDateChanged = {
                            viewModel.setDate(it)
                        })
                    Spacer(modifier = Modifier.width(24.dp))
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = {
                            Text(text = stringResource(id = R.string.duration))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        visualTransformation = if (uiState.item.duration == 0) PlaceholderTransformation(
                            "0"
                        ) else VisualTransformation.None,
                        value = if (uiState.item.duration > 0) uiState.item.duration.toString() else "",
                        onValueChange = {
                            viewModel.setDuration(it.toIntOrNull() ?: 0)
                        })
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    label = {
                        Text(text = stringResource(id = R.string.notes))
                    },

                    visualTransformation = if (uiState.item.notes.isEmpty()) PlaceholderTransformation(
                        stringResource(id = R.string.no_notes)
                    ) else VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    value = uiState.item.notes, onValueChange = {
                        viewModel.setNotes(it)
                    })
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.show_in_diary)
                    )
                    Switch(checked = uiState.item.showInDiary, onCheckedChange = {
                        viewModel.setShowInDiary(it)
                    })
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.saveCare()
                    }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "")
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.save))
                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (uiState.errorMessage != null) {
                    ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
                        viewModel.clearErrors()
                    })
                }

            }
        }
    }
    if (uiState.errorMessage != null){
        ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
            viewModel.clearErrors()
        })
    }
    if (uiState.isSuccess){
        if (plannedActivityId == null && homeCareActivity == null){
            showAlertNotPlannedActivity = true
        }else {
            viewModel.clearData()
            onDismissRequest(true)
        }
    }
    if (showAlertNotPlannedActivity){
        val message = if (uiState.item.showInDiary) stringResource(id = R.string.activity_saved_diary) else stringResource(
            id = R.string.activity_saved_not_visible)
        ErrorAlert(message = message, onDismissRequest = {
            viewModel.clearData()
            onDismissRequest(true)
        })

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PreviewCreateEditCareScreen() {
    AppTheme {
        CreateEditCareScreen(
            onDismissRequest = {

            },
            codCase = ""
        )
    }
}