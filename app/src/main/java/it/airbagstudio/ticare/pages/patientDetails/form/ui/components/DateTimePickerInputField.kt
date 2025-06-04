package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.pages.patientDetails.form.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Un campo di input per selezionare data e ora in sequenza.
 * Mostra prima un DatePickerDialog, poi un TimePickerDialog.
 *
 * @param label Etichetta del campo.
 * @param selectedTimestamp Timestamp (Long) della data e ora selezionata.
 * @param onTimestampSelected Callback invocato con il nuovo timestamp dopo la selezione di data e ora.
 * @param modifier Modificatore per questo Composable.
 * @param error Messaggio di errore da visualizzare sotto il campo, se presente.
 * @param enabled Flag per abilitare/disabilitare il campo.
 * @param dateFormatPattern Pattern per formattare la data e l'ora visualizzate.
 */
@Composable
fun DateTimePickerInputField(
    label: String,
    selectedTimestamp: Long,
    onTimestampSelected: (timestamp: Long) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    dateFormatPattern: String = "dd/MM/yyyy HH:mm"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = selectedTimestamp

    val displayFormatter = remember(dateFormatPattern) { SimpleDateFormat(dateFormatPattern, Locale.getDefault()) }
    val displayDateTime = displayFormatter.format(Date(selectedTimestamp))

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val tempSelectedDateCalendar = remember { Calendar.getInstance() }

    if (showDatePicker.value) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                tempSelectedDateCalendar.clear() // Clear previous selections
                tempSelectedDateCalendar.set(year, month, dayOfMonth)
                showDatePicker.value = false
                showTimePicker.value = true // Trigger time picker
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setOnDismissListener { showDatePicker.value = false }
        datePickerDialog.show()
    }

    if (showTimePicker.value) {
        // Initialize time picker with current hour/minute of the initially selected timestamp
        // or current time if the date was just changed.
        val initialHour = if (tempSelectedDateCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                               tempSelectedDateCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                               tempSelectedDateCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            calendar.get(Calendar.HOUR_OF_DAY)
        } else {
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY) // Default to current hour if date changed
        }
        val initialMinute = if (tempSelectedDateCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                                tempSelectedDateCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                                tempSelectedDateCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            calendar.get(Calendar.MINUTE)
        } else {
            Calendar.getInstance().get(Calendar.MINUTE) // Default to current minute if date changed
        }

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                tempSelectedDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                tempSelectedDateCalendar.set(Calendar.MINUTE, minute)
                tempSelectedDateCalendar.set(Calendar.SECOND, 0)
                tempSelectedDateCalendar.set(Calendar.MILLISECOND, 0)
                onTimestampSelected(tempSelectedDateCalendar.timeInMillis)
                showTimePicker.value = false
            },
            initialHour,
            initialMinute,
            true // 24-hour view
        )
        timePickerDialog.setOnDismissListener { showTimePicker.value = false }
        timePickerDialog.show()
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = displayDateTime,
            onValueChange = { /* Non modificabile direttamente */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) {
                    showDatePicker.value = true
                },
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Event, // Using Event icon for combined date/time
                    contentDescription = "Select Date and Time",
                    modifier = Modifier.clickable(enabled = enabled) { showDatePicker.value = true }
                )
            },
            readOnly = true,
            enabled = enabled,
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "DateTimePickerInputField Preview")
@Composable
fun DateTimePickerInputFieldPreview() {
    AppTheme {
        var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }
        DateTimePickerInputField(
            label = "Data e Ora Compilazione",
            selectedTimestamp = timestamp,
            onTimestampSelected = { newTimestamp ->
                timestamp = newTimestamp
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "DateTimePickerInputField Error Preview")
@Composable
fun DateTimePickerInputFieldWithErrorPreview() {
    AppTheme {
        var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }
        DateTimePickerInputField(
            label = "Data e Ora Evento",
            selectedTimestamp = timestamp,
            onTimestampSelected = { newTimestamp ->
                timestamp = newTimestamp
            },
            error = "Campo obbligatorio",
            modifier = Modifier.padding(16.dp)
        )
    }
}
