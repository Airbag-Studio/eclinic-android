package it.airbagstudio.ticare.pages.otherTreatments

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme
import kotlinx.serialization.json.JsonNull.content

@Composable
fun OtherTreatmentScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = "") {
                onBack()
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(start = 24.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                contentColor = MaterialTheme.colorScheme.primary,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = stringResource(
                            id = R.string.new_treatment
                        )
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(
                            id = R.string.new_treatment
                        )
                    )
                },
                onClick = {

                })
        }
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
private fun BuildHeader(date: String, shiftName: String) {
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
    Spacer(modifier = Modifier.height(16.dp))
    Divider()
}

@Composable
@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun PreviewOtherTreatmentScreen(){
    AppTheme {
        Scaffold {  values ->
            OtherTreatmentScreen {

            }
        }
    }
}