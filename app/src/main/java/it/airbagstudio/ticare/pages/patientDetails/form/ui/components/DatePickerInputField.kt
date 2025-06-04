package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * A composable that displays a date and opens a DatePickerDialog on click.
 * It's styled to look similar to an OutlinedTextField.
 *
 * @param label The label for the date field.
 * @param selectedDateMillis The currently selected date in milliseconds, or null if no date is selected.
 * @param onDateSelected Callback invoked when a date is selected from the dialog, providing the date in milliseconds.
 * @param modifier Modifier for this composable.
 * @param isError Whether the field is currently in an error state.
 * @param hintText The text to display when no date is selected.
 * @param errorTextResId Optional string resource ID for an error message to display below the field when [isError] is true.
 */
@Composable
fun DatePickerInputField(
    label: String,
    selectedDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    hintText: String,
    errorTextResId: Int? = R.string.field_required // Default error message
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    selectedDateMillis?.let { calendar.timeInMillis = it }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val resultCalendar = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            onDateSelected(resultCalendar.timeInMillis)
        }, year, month, dayOfMonth
    )
    // Prevent selecting future dates for Date of Birth or similar fields
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    val borderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
    val labelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant


    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall, // Or bodySmall, adjust as needed
            color = labelColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OutlinedTextFieldDefaults.shape)
                .border(
                    width = 1.dp, // OutlinedTextFieldDefaults.UnfocusedBorderThickness,
                    color = borderColor,
                    shape = OutlinedTextFieldDefaults.shape
                )
                .clickable { datePickerDialog.show() }
                .padding(horizontal = 16.dp, vertical = 12.dp) // Similar to OutlinedTextField padding
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selectedDateMillis != null) {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
                    } else {
                        hintText
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedDateMillis != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Select Date", // TODO: Consider string resource
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        if (isError && errorTextResId != null) {
            Text(
                text = stringResource(id = errorTextResId),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        // Removed the else branch with the Spacer to eliminate extra bottom padding when no error
    }
}
