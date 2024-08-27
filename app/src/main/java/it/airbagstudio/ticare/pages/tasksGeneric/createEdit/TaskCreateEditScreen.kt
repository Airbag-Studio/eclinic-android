package it.airbagstudio.ticare.pages.tasksGeneric.createEdit


import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import ch.ticare.eclinic.library.entity.TaskType
import ch.ticare.eclinic.library.entity.ToolTag
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.NotesPopupButton
import it.airbagstudio.ticare.ui.components.SwitchItem
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.getExecDateTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateEditScreen(
    taskType: TaskType? = null,
    toolTag: ToolTag,
    patientCode: String,
    taskToEdit: AgendaTask?,
    canWrite: Boolean,
    viewModel: TaskCreateEditViewModel = hiltViewModel(),
    onDismissRequest: (Boolean) -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        viewModel.clearData()
        viewModel.canWrite = canWrite
        viewModel.taskType = toolTag
        viewModel.caseCode = patientCode
        if (taskType != null){
            viewModel.setAgendaTaskTypeId(taskType.id)
        }
        if (taskToEdit != null){
            viewModel.setDate(taskToEdit.getExecDateTime() ?: Date())
            viewModel.setDuration(taskToEdit.duration)
            viewModel.setNotes(taskToEdit.notes)
            viewModel.setNotExecuted(taskToEdit.isSkipped)
            viewModel.setShowInDiary(taskToEdit.showInDiary)
            viewModel.setAgendaTaskTypeCode(taskToEdit.typeCode)
            viewModel.setGeneralNote(taskToEdit.schedulerNotes ?: "")
        }
    }
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDismissRequest(false)
        }) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = uiState.taskActivityType?.desc ?: "")
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
                CalendarTextField(
                    modifier = Modifier.fillMaxWidth(),
                    date = uiState.task.dateTime,
                    label = { Text(text = stringResource(id = R.string.actual_date_time)) },
                    onDateChanged = {
                        viewModel.setDate(it)
                    },
                    enabled = uiState.isEditingEnabled
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    label = {
                        Text(stringResource(id = R.string.duration))
                    },
                    value = if ((uiState.task.duration ?: 0) > 0) uiState.task.duration.toString() else "",
                    visualTransformation = if (uiState.task.duration == 0) PlaceholderTransformation("0") else VisualTransformation.None,
                    onValueChange = {
                        viewModel.setDuration(it.toIntOrNull() ?: 0)
                    },
                    enabled = uiState.isEditingEnabled
                )
                Spacer(modifier = Modifier.height(16.dp))
                NotesPopupButton(
                    title = stringResource(id = R.string.general_note),
                    text = uiState.task.generalNote,
                    enabled = true,
                    editable = false,
                    onTextChanged = {})
                NotesPopupButton(text = uiState.task.notes, enabled = uiState.isEditingEnabled, editable = uiState.isEditingEnabled) {
                    viewModel.setNotes(it)
                }
                Spacer(modifier = Modifier.height(16.dp))
                SwitchItem(label =stringResource(id = R.string.show_in_diary) , value = uiState.task.showInDiary, enabled = uiState.isEditingEnabled) {
                    viewModel.setShowInDiary(it)
                }
                SwitchItem(label =stringResource(id = R.string.not_performed) , value = uiState.task.notExecuted, enabled = uiState.isEditingEnabled) {
                    viewModel.setNotExecuted(it)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    enabled = !uiState.isLoading && uiState.task.isValid && uiState.isEditingEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.saveTask(task = taskToEdit)
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
}

/*
@Preview
@Composable
private fun PreviewTaskCreateEditScreen(){
    AppTheme {
        TaskCreateEditScreen(){

        }
    }
}

 */