package it.airbagstudio.ticare.ui.components.timeTracker


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R

@Composable
fun StopTrackerDialog(startTime: String,totalWorkingTime: String,onDismissRequest: (Boolean) -> Unit){
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.stop_work)) },
        text = {
               Column {
                   Text(
                       text = stringResource(id = R.string.start_date_time),
                       style = MaterialTheme.typography.titleSmall
                   )
                   Text(
                       text = startTime,
                       style = MaterialTheme.typography.bodyMedium
                   )
                   Spacer(modifier = Modifier.height(16.dp))
                   Text(
                       text = stringResource(id = R.string.working_time),
                       style = MaterialTheme.typography.titleSmall
                   )
                   Text(
                       text = totalWorkingTime,
                       style = MaterialTheme.typography.bodyMedium
                   )
                   Spacer(modifier = Modifier.height(16.dp))
                   Text(
                       text = stringResource(id = R.string.stop_work_confirm_message),
                       style = MaterialTheme.typography.bodyMedium
                   )
               }
        },
        onDismissRequest = {
            onDismissRequest(false)
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest(true)
            }) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest(false)
            }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}