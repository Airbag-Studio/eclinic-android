package it.airbagstudio.ticare.pages.otherTreatments.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getExecDateTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTreatmentScreen(
    state: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildSheetContent(date: Date?,duration: MutableState<Int>,isLoading:Boolean = false){
    var selectedDate by remember {
        mutableStateOf(date ?: Date())
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedDate.hours,
        initialMinute = selectedDate.minutes
    )
    val column1Weight = 0.6f
    val column2Weight = 1 - column1Weight
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Consigli e istruzioni CAT",
            style = MaterialTheme.typography.titleLarge
        )
        Row() {
            Text(
                text = "LAMal",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "5010",
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .clickable {
                        showDatePicker = true
                    }
                    .weight(column1Weight),
                value = selectedDate.format("dd MMMM yyyy, HH:mm "),
                enabled = false,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ) ,
                onValueChange = {},
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_field_calendar),
                        stringResource(id = R.string.actual_date_time)
                    )
                },
                label = { Text(text = stringResource(id = R.string.actual_date_time)) }
            )
            Spacer(modifier = Modifier.width(24.dp))
            OutlinedTextField(
                maxLines = 1,
                label = {
                        Text(text = stringResource(id = R.string.duration))
                },
                modifier = Modifier.weight(column2Weight),
                value = duration.toString(), onValueChange = {
                duration.value = it.toIntOrNull() ?: 0
            })
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.height(500.dp)) {
            if (showDatePicker) {
                Column() {
                    DatePicker(
                        dateValidator = {
                            val date = Date(it)
                            date.before(Date())
                        },
                        state = datePickerState,
                        headline = null,
                        title = null,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor =MaterialTheme.colorScheme.primary,
                            selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            currentYearContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Row() {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = { showDatePicker = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                showTimePicker = true
                                showDatePicker = false
                            }) {
                            Text(
                                text = stringResource(id = R.string.ok)
                            )
                        }
                    }
                }

            } else if (showTimePicker) {
                Column(
                    modifier = Modifier.padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectorColor = MaterialTheme.colorScheme.primary,
                            timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                    Row() {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                showTimePicker = false
                                showDatePicker = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                selectedDate =
                                    if (datePickerState.selectedDateMillis != null) Date(
                                        datePickerState.selectedDateMillis!!
                                    ) else Date()
                                selectedDate.hours = timePickerState.hour
                                selectedDate.minutes = timePickerState.minute

                                showTimePicker = false
                                showDatePicker = false

                            }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    }
                }

            } else {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            maxLines = 1,
                            modifier = Modifier.weight(column1Weight),
                            value = "",
                            label = {
                                Text(text = stringResource(id = R.string.guarantor))
                            },
                            onValueChange = {

                            })

                        Spacer(modifier = Modifier.width(24.dp))
                        OutlinedTextField(
                            maxLines = 1,
                            modifier = Modifier.weight(column2Weight),
                            value = "",
                            label = {
                                Text(text = stringResource(id = R.string.quantity))
                            },
                            onValueChange = {

                            })
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        label = {
                                Text(text = stringResource(id = R.string.notes))
                        },
                        value = "", onValueChange = {

                        })
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.execute)
                        )
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.save))
                        if (isLoading) {
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
    }
}

@Composable
@Preview
private fun ContentPreview(){
    AppTheme {
        BuildSheetContent(null, remember {
            mutableStateOf(0)
        })
    }
}