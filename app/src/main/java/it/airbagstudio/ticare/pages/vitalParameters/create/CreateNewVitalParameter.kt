package it.airbagstudio.ticare.pages.vitalParameters.create

import android.util.Log
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.AgendaTask
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.getExecDateTime
import it.airbagstudio.ticare.utils.isValidVitalParameterValue
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewVitalParameterSheet(
    task: AgendaTask? = null,
    vitalSignCode: String? = null,
    caseCode: String? = null,
    viewModel: CreateNewVitalParameterSheetViewModel = hiltViewModel(),
    onDismissRequest: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.clearData()
        viewModel.agendaTask = task
        viewModel.caseCode = caseCode
        if (vitalSignCode != null) {
            viewModel.setVitalSignCode(vitalSignCode)
        } else if (task != null) {
            viewModel.caseCode = task.caseCode
            viewModel.setDate(task.getExecDateTime() ?: Date())
            viewModel.setDuration(task.duration)
            viewModel.setNotes(task.notes)
            viewModel.setVitalSignCode(task.typeCode)
            viewModel.setValue(task.value ?: "")
            viewModel.setShowInDiary(task.showInDiary)
            viewModel.setNotExecuted(task.isSkipped)
        }

    }
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            viewModel.clearData()
            onDismissRequest(false)
        }) {
        BuildSheetContent(viewModel, onDismissRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildSheetContent(
    viewModel: CreateNewVitalParameterSheetViewModel,
    onDismissRequest: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(text = uiState.description)
                    }

                },
                actions = {
                    IconButton(onClick = { onDismissRequest(false) }) {
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
            Row {
                OutlinedTextField(
                    enabled = uiState.isEditingEnabled,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    label = {
                        Text(uiState.mUSymbol)
                    },
                    isError = !uiState.value.isValidVitalParameterValue() && !uiState.notExecuted,
                    value = uiState.value,
                    visualTransformation = if (uiState.value.isEmpty()) PlaceholderTransformation("0") else VisualTransformation.None,
                    onValueChange = {
                        viewModel.setValue(it)
                    }
                )
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedTextField(
                    enabled = uiState.isEditingEnabled,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    label = {
                        Text(stringResource(id = R.string.duration))
                    },
                    value = if (uiState.duration > 0) uiState.duration.toString() else "",
                    visualTransformation = if (uiState.duration == 0) PlaceholderTransformation("0") else VisualTransformation.None,
                    onValueChange = {
                        viewModel.setDuration(it.toIntOrNull() ?: 0)
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Log.d("uiState.date", uiState.date.toString())
            CalendarTextField(
                enabled = uiState.isEditingEnabled,
                modifier = Modifier.fillMaxWidth(),
                date = uiState.date,
                label = { Text(text = stringResource(id = R.string.actual_date_time)) },
                onDateChanged = {
                    viewModel.setDate(it)
                })
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                enabled = uiState.isEditingEnabled,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                label = {
                    Text(text = stringResource(id = R.string.notes))
                },
                value = uiState.notes,
                onValueChange = {
                    viewModel.setNotes(it)
                })
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.show_in_diary)
                )
                Switch(
                    enabled = uiState.isEditingEnabled,
                    checked = uiState.showInDiary,
                    onCheckedChange = {
                    viewModel.setShowInDiary(it)
                })
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.not_performed)
                )
                Switch(
                    enabled = uiState.isScheduled,
                    checked = uiState.notExecuted,
                    onCheckedChange = {
                        viewModel.setNotExecuted(it)
                    })
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                enabled = (!uiState.isLoading && (uiState.value.isValidVitalParameterValue() || (uiState.notExecuted && uiState.notes.isNotEmpty())) && uiState.isEditingEnabled),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.saveTask()
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
                    viewModel.clearError()
                })
            }
            if (uiState.isSuccess) {
                viewModel.clearData()
                onDismissRequest(true)
            }
        }
    }
}