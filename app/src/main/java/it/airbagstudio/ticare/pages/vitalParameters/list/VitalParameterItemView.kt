package it.airbagstudio.ticare.pages.vitalParameters.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.AgendaTask
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.DrugChip
import it.airbagstudio.ticare.ui.components.LabelValueRow
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.checkGreen
import it.airbagstudio.ticare.ui.theme.redColor

data class VitalParameterItem(
    val name: String,
    val typeMsmUnit: String,
    val quantity: String,
    val time: String,
    val executed:Boolean,
    val isConfirmed: Boolean,
    val item: AgendaTask?,
    val hasDataToUpload: Boolean,
    val isSkipped: Boolean
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VitalParameterItemView(item: VitalParameterItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 0.dp, bottom = 0.dp)
    ) {
        Row {
            if (item.executed || item.hasDataToUpload) {
                Column(modifier = Modifier.padding(end = 8.dp)) {
                    if (item.executed) {
                        Icon(
                            tint = checkGreen,
                            imageVector = Icons.Default.Check, contentDescription = ""
                        )
                    }
                    if (item.hasDataToUpload) {
                        Spacer(modifier = Modifier.padding(top = 2.dp))
                        OfflineSyncImage(hasOfflineData = true, hasDataToSync = true)
                    }
                }
            }
            Column {
                Row {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = item.name
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(end = 24.dp)
                ) {
                    LabelValueRow(
                        label = item.typeMsmUnit,
                        value = item.quantity
                    )
                }
                Row() {
                    LabelValueRow(
                        label = stringResource(id = R.string.time),
                        value = item.time
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (!item.isConfirmed || item.isSkipped) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (!item.isConfirmed) {
                            DrugChip(
                                label = stringResource(id = R.string.not_confirmed),
                                textColor = redColor
                            )
                        }
                        if (item.isSkipped) {
                            DrugChip(label = stringResource(id = R.string.not_performed))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
    }
}



@Composable
@Preview
private fun PreviewVitalParameterItem(){
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                VitalParameterItemView(item = VitalParameterItem("Frequenza Cardiaca", typeMsmUnit = "Fr/min", quantity = "72",time = "09:30",executed = true,false,null,true, isSkipped = true)) {

                }
                VitalParameterItemView(item = VitalParameterItem("Frequenza Cardiaca", typeMsmUnit = "Fr/min", quantity = "72",time = "09:30",executed = false,false,null,false, isSkipped = false)) {

                }
            }
        }

    }
}