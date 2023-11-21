package it.airbagstudio.ticare.ui.components.timeTracker

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R

@Composable
fun StartTrackerDialog(
    onDismissRequest: (Boolean) -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.start_work)) },
        text = { Text(text = stringResource(id = R.string.start_work_from_care_planes)) },
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