package it.airbagstudio.ticare.pages.drugsAdministration

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.shimmerBrush
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.checkGreen
import it.airbagstudio.ticare.ui.theme.tertiary95

@Composable
fun DrugAdministrationItemView(
    name: String,
    quantity: Int,
    time: String,
    reserves: Int? = null,
    isCompleted: Boolean,
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

        val alpha = if (isCompleted) 0.5f else 1f

        Row() {
            if (isCompleted) {
                Icon(
                    modifier = Modifier.padding(end = 16.dp),
                    tint = checkGreen,
                    imageVector = Icons.Default.Check, contentDescription = ""
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f).alpha(alpha)
            )
            if (!isCompleted) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = name
                )
            }

        }
        Row(modifier = Modifier.alpha(alpha)) {
            if (isCompleted) {
                Spacer(modifier = Modifier.width(40.dp))
            }
            LabelValueRow(label = stringResource(id = R.string.quantity), value = "$quantity")
            if (reserves != null) {
                Spacer(modifier = Modifier.weight(1f))
                LabelValueRow(label = stringResource(id = R.string.reserves), value = "$reserves")
                Spacer(modifier = Modifier.width(40.dp))

            }
        }
        Row(modifier = Modifier.alpha(alpha)) {
            if (isCompleted) {
                Spacer(modifier = Modifier.width(40.dp))
            }
            LabelValueRow(label = stringResource(id = R.string.time), value = time)
        }

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
fun DrugAdministrationItemViewLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 24.dp, bottom = 12.dp)
    ) {

        Text(
            text = "",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 40.dp)
                .background(shimmerBrush())
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(12.dp)
                .background(shimmerBrush())
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(12.dp)
                .background(shimmerBrush())
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
            isCompleted = false,
            reserves = 2
        ) {}
    }
}

@Composable
@Preview
private fun PreviewDrugAdministrationItemCompleted() {
    AppTheme() {
        DrugAdministrationItemView(
            name = "Meto Zeroch cpr ret 25mg",
            quantity = 4,
            time = "10:30",
            isCompleted = true,
            reserves = 2
        ) {}
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
            isCompleted = false,
            isReserve = true
        ) {}
    }
}