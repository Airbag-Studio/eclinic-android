package it.airbagstudio.ticare.pages.patientDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.data.AlertItem

data class AllergiesItem(val name: String, val isDrug:Boolean)
@Composable
fun AlertChip(alert: AlertItem,onClick:()-> Unit) {
    AssistChip(
        border = null,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(alert.colorBg.toColorInt()),
            labelColor = Color(alert.colorFg.toColorInt()),

            ),
        label = {
            Text(
                text = alert.label,
                style = MaterialTheme.typography.bodySmall
            )
        },
        onClick = onClick
    )
}

@Composable
fun AllergyChip(item: AllergiesItem) {
    AssistChip(
        border = BorderStroke(1.dp,MaterialTheme.colorScheme.onSurfaceVariant),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,

            ),
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.isDrug) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chip_pill),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }

        },
        onClick = {}
    )
}