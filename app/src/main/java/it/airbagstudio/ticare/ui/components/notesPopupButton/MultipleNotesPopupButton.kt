package it.airbagstudio.ticare.ui.components.notesPopupButton

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import it.airbagstudio.ticare.R

data class MultipleNotesPopupItem(
    val title: String,
    val text: String?
)

@Composable
fun MultipleNotesPopupButton(title: String = stringResource(id = R.string.notes), notes: List<MultipleNotesPopupItem>) {
    var showNotesPopup by remember {
        mutableStateOf(false)
    }
    TextButton(
        shape = RectangleShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        onClick = { showNotesPopup = true }
    ) {
        val buttonText = notes.firstOrNull { it.text?.isNotEmpty() == true }?.text ?: stringResource(id = R.string.no_notes)
        NotesPopupButtonContent(title = title, text = buttonText)
    }
    if (showNotesPopup){
        MultipleNotesPopup(
            title = title,
            notes = notes,
            onDismiss = {
                showNotesPopup = false
            }
        )
    }
}