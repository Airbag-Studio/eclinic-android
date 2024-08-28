package it.airbagstudio.ticare.ui.components.notesPopupButton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import it.airbagstudio.ticare.R

@Composable
fun NotesPopupButtonContent(title: String, text:String){
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