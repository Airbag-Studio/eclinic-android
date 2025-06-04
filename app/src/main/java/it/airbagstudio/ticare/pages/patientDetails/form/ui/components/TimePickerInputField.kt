package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
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
import it.airbagstudio.ticare.pages.patientDetails.form.ui.theme.AppTheme // Ensure this import is correct
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Un campo di input per selezionare l'ora, che mostra un TimePickerDialog al tocco.
 *
 * @param label Etichetta del campo.
 * @param selectedHour Ora selezionata (0-23).
 * @param selectedMinute Minuto selezionato (0-59).
 * @param onTimeSelected Callback invocato quando un'ora viene selezionata.
 * @param modifier Modificatore per questo Composable.
 * @param error Messaggio di errore da visualizzare sotto il campo, se presente.
 * @param enabled Flag per abilitare/disabilitare il campo.
 * @param placeholder Testo segnaposto da visualizzare se non è selezionata alcuna ora.
 */
@Composable
fun TimePickerInputField(
    label: String,
    selectedHour: Int,
    selectedMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    placeholder: String = "HH:mm"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
    calendar.set(Calendar.MINUTE, selectedMinute)

    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val displayTime = timeFormatter.format(calendar.time)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(hourOfDay, minute)
        },
        selectedHour,
        selectedMinute,
        true // true for 24-hour view
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = displayTime,
            onValueChange = { /* Non modificabile direttamente */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) {
                    timePickerDialog.show()
                },
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Select Time",
                    modifier = Modifier.clickable(enabled = enabled) { timePickerDialog.show() }
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

@Preview(showBackground = true, name = "TimePickerInputField Preview")
@Composable
fun TimePickerInputFieldPreview() {
    AppTheme {
        var hour by remember { mutableStateOf(14) }
        var minute by remember { mutableStateOf(30) }
        TimePickerInputField(
            label = "Ora Compilazione",
            selectedHour = hour,
            selectedMinute = minute,
            onTimeSelected = { h, m ->
                hour = h
                minute = m
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "TimePickerInputField Error Preview")
@Composable
fun TimePickerInputFieldWithErrorPreview() {
    AppTheme {
        var hour by remember { mutableStateOf(9) }
        var minute by remember { mutableStateOf(0) }
        TimePickerInputField(
            label = "Ora Inizio",
            selectedHour = hour,
            selectedMinute = minute,
            onTimeSelected = { h, m ->
                hour = h
                minute = m
            },
            error = "L'ora è obbligatoria",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "TimePickerInputField Disabled Preview")
@Composable
fun TimePickerInputFieldDisabledPreview() {
    AppTheme {
        TimePickerInputField(
            label = "Ora (Disabilitato)",
            selectedHour = 10,
            selectedMinute = 0,
            onTimeSelected = { _, _ -> },
            enabled = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}
