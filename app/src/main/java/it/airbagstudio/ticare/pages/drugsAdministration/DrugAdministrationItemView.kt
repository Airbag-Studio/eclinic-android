package it.airbagstudio.ticare.pages.drugsAdministration

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import it.airbagstudio.ticare.ui.theme.tertiary95

@Composable
fun DrugAdministrationItemView(
    name: String,
    quantity: Int,
    time: String,
    reserves: Int? = null,
    isReserve: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .background(if (isReserve) tertiary95 else MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 24.dp, bottom = 12.dp)
    ) {
        Row() {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = name
            )

        }
        Row() {
            LabelValueRow(label = stringResource(id = R.string.quantity), value = "$quantity")
            if (reserves != null) {
                Spacer(modifier = Modifier.weight(1f))
                LabelValueRow(label = stringResource(id = R.string.reserves), value = "$reserves")
                Spacer(modifier = Modifier.width(40.dp))

            }
        }
        LabelValueRow(label = stringResource(id = R.string.time), value = time)


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
private fun PreviewDrugAdministrationItem() {
    AppTheme() {
        DrugAdministrationItemView(
            name = "Meto Zeroch cpr ret 25mg",
            quantity = 4,
            time = "10:30",
            reserves = 2
        ){}
    }
}

@Composable
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
private fun PreviewDrugAdministrationReserveItem() {
    AppTheme() {
        DrugAdministrationItemView(
            name = "Meto Zeroch cpr ret 25mg",
            quantity = 4,
            time = "10:30",
            isReserve = true
        ){}
    }
}