package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun NotesPopupButton(title: String = stringResource(id = R.string.notes),text: String, enabled: Boolean,editable:Boolean, onTextChanged: (String) -> Unit) {
    var showNotesPopup by remember {
        mutableStateOf(false)
    }
    if (editable){
        OutlinedButton(
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),

            onClick = {
                showNotesPopup = true
            }
        ) {
            ButtonContent(title = title, text = if (text.isNotEmpty()) text else stringResource(id = R.string.no_notes))
        }
    }else{
        TextButton(
            shape = RectangleShape,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            onClick = { showNotesPopup = true }
        ) {
            ButtonContent(title = title, text = if (text.isNotEmpty()) text else stringResource(id = R.string.no_notes))
        }
    }

    if (showNotesPopup){
        NotesPopup(title = title, startingText = text, editable = editable, onDone = {newText ->
            showNotesPopup = false
            newText?.let{
                onTextChanged(it)
            }
        })
    }
}
@Composable
private fun ButtonContent(title: String,text:String){
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        Row() {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = stringResource(
                    id = R.string.notes
                )
            )

        }
        Text(
            text = text,
            maxLines = 2,
            minLines = 2,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis
        )
    }
}
@Composable
@Preview
private fun PreviewNoteButton(){
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                NotesPopupButton(
                    text = "",
                    enabled = true,
                    editable = true,
                ){

                }
                NotesPopupButton(
                    text = "",
                    enabled = true,
                    editable = false,
                ){

                }
            }
        }

    }
}