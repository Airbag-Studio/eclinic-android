package it.airbagstudio.ticare.ui.components.notesPopupButton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ToolbarWithBack
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun MultipleNotesPopup(title: String = stringResource(id = R.string.notes), notes: List<MultipleNotesPopupItem>,onDismiss: () -> Unit){
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                ToolbarWithBack(title = title, onBack = onDismiss)
            }) { values ->
            LazyColumn(modifier = Modifier.padding(values)) {
                items(notes.filter { it.text?.isNotEmpty() == true }){
                    NoteItem(item = it)
                }
            }
        }
    }
}

@Composable
private fun NoteItem(item: MultipleNotesPopupItem){
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp).padding(bottom = 16.dp)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(text = item.text ?: "")
    }
}

@Composable
@Preview
private fun PreviewMultipleNotesPopup(){
    AppTheme {
        MultipleNotesPopup(
            title = stringResource(id = R.string.notes),
            notes = listOf(
                MultipleNotesPopupItem(
                    title = "title",
                    text = null
                ),
                MultipleNotesPopupItem(
                    title = "title",
                    text = "text"
                )
            )
        ){}
    }
}