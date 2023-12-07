package it.airbagstudio.ticare.pages.workinghours.create

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
import androidx.compose.material3.Divider
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
import ch.ticare.eclinic.library.entity.EmployeeWorkingHour
import com.google.android.datatransport.runtime.util.PriorityMapping.toInt
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.toDate
import kotlinx.datetime.LocalTime
import java.time.Duration
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkingHoursItemCreate(
    viewModel: WorkingHoursItemCreateViewModel = hiltViewModel(),
    typeId: Int?,
    employeeWorkingHour: EmployeeWorkingHour?,
    onDismissRequest: (Boolean) -> Unit
){
    LaunchedEffect(Unit) {
        if (typeId != null) {
            Log.d("typeId",typeId.toString())
            viewModel.setSelectedTypeId(typeId)
        }
        if (employeeWorkingHour != null){
            viewModel.setSelectedTypeId(employeeWorkingHour.idType)
            viewModel.setId(employeeWorkingHour.id)
            val duration = employeeWorkingHour.totalHours.split(":").let {
                val hours = it[0].trim().toInt()
                val minutes = it[1].trim().toInt()
                hours * 60 + minutes
            }
            viewModel.setDuration(duration)
            viewModel.setDate(employeeWorkingHour.date.toDate("dd.MM.yyyy") ?: Date())
        }
    }
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            viewModel.clearData()
            onDismissRequest(false)
        }

    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            ) {
                Divider()
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row {
                        CalendarTextField(
                            enabled = employeeWorkingHour != null,
                            modifier = Modifier.weight(1f),
                            date = uiState.item.date,
                            showTime = false,
                            label = {
                                Text(text = stringResource(id = R.string.actual_date))
                            },
                            onDateChanged = {
                                viewModel.setDate(it)
                            })
                        Spacer(modifier = Modifier.width(16.dp))

                        OutlinedTextField(
                            enabled = employeeWorkingHour != null,
                            singleLine = true,
                            modifier = Modifier.width(120.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            visualTransformation = if (uiState.item.duration == 0) PlaceholderTransformation(
                                "0"
                            ) else VisualTransformation.None,
                            label = {
                                Text(text = stringResource(id = R.string.duration))
                            },
                            value = uiState.item.duration.toString(),
                            onValueChange = {
                                viewModel.setDuration(it.toIntOrNull() ?: 0)
                            }
                        )

                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        enabled = !uiState.isLoading && uiState.isValid,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.saveWorkingHour()
                        }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.execute)
                        )
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
                    Spacer(modifier = Modifier.height(24.dp))
                }

            }
        }
        if (uiState.isSuccess) {
            viewModel.clearData()
            onDismissRequest(true)

        }
        if (uiState.errorMessage != null){
            ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
                viewModel.clearErrors()
            })
        }
    }
}