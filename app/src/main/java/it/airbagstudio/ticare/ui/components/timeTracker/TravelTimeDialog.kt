package it.airbagstudio.ticare.ui.components.timeTracker

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.airbagstudio.ticare.R

@Composable
fun TravelTimeDialog(travelTime: Long, onDismissRequest: (Boolean) -> Unit) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.travel_time))
        },
        text = {
            Text(text = stringResource(id = R.string.travel_time_text,"${travelTime}m"))
        },
        onDismissRequest = { onDismissRequest(false) },
        confirmButton = {
            TextButton(onClick = { onDismissRequest(true) }) {
                Text(text = stringResource(id = R.string.add_travel_time))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest(false) }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}