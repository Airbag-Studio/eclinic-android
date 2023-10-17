package it.airbagstudio.ticare.pages.otherTreatments

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync

@Composable
fun OtherTreatmentScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = "") {
                onBack()
            }
        },
    ) { values ->
        Column(
            Modifier
                .padding(values)
                .verticalScroll(rememberScrollState())
        ) {
            BuildHeader(date = "", shiftName = "")
        }
    }


}

@Composable
private fun BuildHeader(date:String,shiftName:String){
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(id = R.string.other_prescriptions),
        style = MaterialTheme.typography.headlineSmall

    )
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "$date - $shiftName",
        style = MaterialTheme.typography.labelMedium
    )
    Divider()
}