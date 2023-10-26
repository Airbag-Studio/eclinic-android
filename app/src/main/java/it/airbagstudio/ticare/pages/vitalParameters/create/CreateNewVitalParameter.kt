package it.airbagstudio.ticare.pages.vitalParameters.create

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.AgendaTask
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.getExecDateTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewVitalParameterSheet(
    sheetState: SheetState,
    task: AgendaTask? = null,
    vitalSignCode: String? = null,
    caseCode: String? = null,
    viewModel: CreateNewVitalParameterSheetViewModel = hiltViewModel(),
    onDismissRequest: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.agendaTask = task
        viewModel.caseCode = caseCode
        if (vitalSignCode != null){
            viewModel.setVitalSignCode(vitalSignCode)
        }else if (task != null){
            viewModel.setDate(task.getExecDateTime() ?: Date())
            viewModel.setDuration(task.duration)
            viewModel.setNotes(task.notes)
            viewModel.setVitalSignCode(task.typeCode)
            viewModel.setValue(task.value ?: "")
            viewModel.setShowInDiary(task.showInDiary)
        }

    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            viewModel.clearData()
            onDismissRequest(false)
        }) {
        BuildSheetContent(viewModel,onDismissRequest)
    }
}
@Composable
private fun BuildSheetContent(viewModel: CreateNewVitalParameterSheetViewModel,onDismissRequest: (Boolean) -> Unit){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp - 130
    Column(modifier = Modifier
        .height(screenHeight.dp)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
        Text(
            text = uiState.description,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = {
                    Text(uiState.mUSymbol)
                },
                isError = uiState.value.toDoubleOrNull() == null,
                value = uiState.value,
                visualTransformation = if (uiState.value.isEmpty()) PlaceholderTransformation("0") else VisualTransformation.None,
                onValueChange = {
                    viewModel.setValue(it)
                }
            )
            Spacer(modifier = Modifier.width(24.dp))
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = {
                    Text(stringResource(id = R.string.duration))
                },
                value = if(uiState.duration > 0) uiState.duration.toString() else "",
                visualTransformation = if (uiState.duration == 0) PlaceholderTransformation("0") else VisualTransformation.None,
                onValueChange = {
                    viewModel.setDuration(it.toIntOrNull() ?: 0)
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Log.d("uiState.date",uiState.date.toString())
        CalendarTextField(
            modifier = Modifier.fillMaxWidth(),
            date = uiState.date,
            label = { Text(text = stringResource(id = R.string.actual_date_time)) },
            onDateChanged = {
                viewModel.setDate(it)
            })
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
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
                text = stringResource(id = R.string.show_in_diary))
            Switch(checked = uiState.showInDiary, onCheckedChange = {
                viewModel.setShowInDiary(it)
            })
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            enabled = (!uiState.isLoading && uiState.value.toDoubleOrNull() != null),
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
        if (uiState.errorMessage != null){
            ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
                viewModel.clearError()
            })
        }
        if (uiState.isSuccess){
            viewModel.clearData()
            onDismissRequest(true)
        }

    }
}