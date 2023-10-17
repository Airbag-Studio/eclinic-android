package it.airbagstudio.ticare.pages.otherTreatments.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTreatmentScreen(
    state: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        
    }
}

@Composable
private fun BuildSheetContent(){
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Consigli e istruzioni CAT",
            style = MaterialTheme.typography.titleLarge
        )
        Row() {
            Text(
                text = "LAMal",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "5010",
                style = MaterialTheme.typography.titleSmall
            )
        }
        Row() {

        }
    }
}