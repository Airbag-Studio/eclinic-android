package it.airbagstudio.ticare.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.airbagstudio.ticare.R

@Composable
fun ErrorAlert(message:String,onDismissRequest: () -> Unit,onRetry:(() -> Unit)? = null){
    val dismissButton :  @Composable() (() -> Unit) = {
        if (onRetry != null) {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onRetry()
                }) {
                Text(stringResource(id = R.string.retry))
            }
        }
    }


    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = { Text(text = stringResource(id = R.string.warning)) },
        text = { Text(text = message) },
        dismissButton = {
            dismissButton()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }) {
                Text("Ok")
            }
        }

    )
}