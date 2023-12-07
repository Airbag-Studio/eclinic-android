package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import io.ktor.util.reflect.instanceOf
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getExecDateTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showTime: Boolean = true,
    date: Date?,
    label: @Composable() (() -> Unit)?,
    onDateChanged: (Date) -> Unit
) {

    val focusManager = LocalFocusManager.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date?.time
    )
    val timePickerState = rememberTimePickerState(
        initialHour = date?.hours ?: 0,
        initialMinute = date?.minutes ?: 0
    )
    var fieldPosition by remember { mutableStateOf(Offset.Zero) }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var showTimePicker by remember {
        mutableStateOf(false)
    }
    var selectedDate by remember {
        mutableStateOf(date ?: Date())
    }
    val pattern = if (showTime){
        "dd MMMM yyyy, HH:mm "
    }else{
        "dd MMMM yyyy"
    }
    Box(modifier = modifier) {
        OutlinedTextField(
            enabled = enabled,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().onGloballyPositioned {
                fieldSize = it.size
                fieldPosition = it.positionInRoot()
            },
            value = date?.format(pattern)
                ?: selectedDate.format(pattern),
            onValueChange = {},
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_field_calendar),
                    stringResource(id = R.string.actual_date_time)
                )
            },
            label = label
        )
        Box(modifier = Modifier
            .matchParentSize()
            .clickable {
                focusManager.clearFocus(true)
                if (enabled) {
                    showDatePicker = true
                }
            }) {
        }
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                showTimePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePicker = true
                        if (showTime) {
                            showDatePicker = false
                        }else{
                            selectedDate =
                                if (datePickerState.selectedDateMillis != null) Date(
                                    datePickerState.selectedDateMillis!!
                                ) else Date()
                            selectedDate.hours = timePickerState.hour
                            selectedDate.minutes = timePickerState.minute
                            showTimePicker = false
                            showDatePicker = false
                            onDateChanged(selectedDate)
                        }


                    }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        showTimePicker = false
                    }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        ) {
            DatePicker(
                headline = null,
                title = null,
                showModeToggle = false,
                state = datePickerState
            )
        }
    }
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                showTimePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate =
                            if (datePickerState.selectedDateMillis != null) Date(
                                datePickerState.selectedDateMillis!!
                            ) else Date()
                        selectedDate.hours = timePickerState.hour
                        selectedDate.minutes = timePickerState.minute
                        showTimePicker = false
                        showDatePicker = false
                        onDateChanged(selectedDate)
                    }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        showTimePicker = false
                    }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        ) {
            TimePicker(
                state = timePickerState
            )
        }
    }
}

@Composable
private fun BuildButtonsStack(onDiscard: () -> Unit, onConfirm: () -> Unit) {
    Row() {
        Spacer(modifier = Modifier.weight(1f))
        TextButton(
            onClick = {
                onDiscard()
            }
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }
        TextButton(
            onClick = {
                onConfirm()

            }) {
            Text(text = stringResource(id = R.string.ok))
        }
    }
}

@Composable
@Preview
private fun PreviewContent() {
    AppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "TEst")
            Text(text = "TEst")
            Text(text = "TEst")
            Text(text = "TEst")
            Text(text = "TEst")
            Spacer(modifier = Modifier.height(50.dp))
            CalendarTextField(date = null, label = { Text(text = "Data e ora") }) {}
            Text(text = "TEst")
        }
    }
}