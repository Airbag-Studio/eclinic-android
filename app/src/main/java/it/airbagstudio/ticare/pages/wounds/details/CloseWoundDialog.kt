package it.airbagstudio.ticare.pages.wounds.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.PlaceholderTransformation

@Composable
fun CloseWoundDialog(onDismissRequest:(Boolean,String?) -> Unit){
    var note by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = {
        onDismissRequest(false,null)
    }) {
        Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp))) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.closing_protocol),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.wound_closing_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    visualTransformation = if (note.isEmpty()) PlaceholderTransformation("  ") else VisualTransformation.None,

                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    label = {
                        Text(text = stringResource(id = R.string.notes))
                    },
                    placeholder = {
                        Text(text = stringResource(id = R.string.no_notes))
                    },
                    value = note,
                    onValueChange = {
                        note = it
                    })
                Spacer(modifier = Modifier.height(16.dp))
            }

            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = {
                    onDismissRequest(false,null)
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                TextButton(
                    enabled = note.isNotEmpty(),
                    onClick = {
                    onDismissRequest(true,note)
                }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
@Preview
private fun PreviewCloseWoundDialog(){
    AppTheme {
        CloseWoundDialog(onDismissRequest = {success,note ->

        })
    }
}