package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ToolbarWithBack

@Composable
fun NoteScreen(text: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            ToolbarWithBack(title = stringResource(id = R.string.scheduling_notes)) {
                onBack()
            }
        }) { values ->
        Column(modifier = Modifier.padding(values)) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp),
                text = text
            )
        }
    }
}