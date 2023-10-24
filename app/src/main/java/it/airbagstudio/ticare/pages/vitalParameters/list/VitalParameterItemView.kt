package it.airbagstudio.ticare.pages.vitalParameters.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.checkGreen

data class VitalParameterItem(
    val name: String,
    val typeMsmUnit: String,
    val quantity: String,
    val time: String
)

@Composable
fun VitalParameterItemView(item: VitalParameterItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 0.dp, bottom = 0.dp)
    ) {
        Row(Modifier.padding(end = 24.dp)) {
            Icon(
                modifier = Modifier.padding(end = 16.dp),
                tint = checkGreen,
                imageVector = Icons.Default.Check, contentDescription = ""
            )
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

                Spacer(modifier = Modifier.width(40.dp))

            LabelValueRow(
                label = item.typeMsmUnit,
                value = item.quantity
            )
        }
        Row() {
                Spacer(modifier = Modifier.width(40.dp))
            LabelValueRow(
                label = stringResource(id = R.string.time),
                value = item.time
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
    }
}

@Composable
private fun LabelValueRow(label: String, value: String) {
    Row() {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
@Preview
private fun PreviewVitalParameterItem(){
    AppTheme {
        VitalParameterItemView(item = VitalParameterItem("Frequenza Cardiaca", typeMsmUnit = "Fr/min", quantity = "72",time = "09:30")) {

        }
    }
}