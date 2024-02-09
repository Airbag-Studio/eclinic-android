package it.airbagstudio.ticare.pages.drugsAdministration

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.DrugChip
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.components.shimmerBrush
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.checkGreen
import it.airbagstudio.ticare.ui.theme.redColor
import it.airbagstudio.ticare.ui.theme.tertiary95

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DrugAdministrationItemView(
    name: String,
    quantity: Double,
    time: String,
    isConfirmed: Boolean,
    notExecuted: Boolean,
    rejected: Boolean,
    reserves: Double? = null,
    isCompleted: Boolean,
    isReserve: Boolean = false,
    hasDataToUpload: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .background(if (isReserve) tertiary95 else MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 0.dp, bottom = 0.dp)
    ) {

        val alpha = if (isCompleted) 0.5f else 1f

        Row(Modifier.padding(end = 8.dp)) {

            if (isCompleted || hasDataToUpload) {
                Column(Modifier.padding(end = 8.dp)) {
                    if (isCompleted){
                        Icon(
                            tint = checkGreen,
                            imageVector = Icons.Default.Check, contentDescription = ""
                        )
                    }
                    if (hasDataToUpload){
                        Spacer(modifier = Modifier.height(2.dp))
                        OfflineSyncImage(hasOfflineData = false, hasDataToSync = hasDataToUpload)
                    }
                }

            }
            Column {
                Row {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f)
                            .alpha(alpha)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = name
                    )
                }
                Row(
                    modifier = Modifier
                        .alpha(alpha)
                        .padding(end = 24.dp)
                ) {
                    LabelValueRow(label = stringResource(id = R.string.quantity), value = "$quantity")
                    if (!isCompleted && !isReserve) {
                        Spacer(modifier = Modifier.weight(1f))
                        LabelValueRow(
                            label = stringResource(id = R.string.reserves),
                            value = "${reserves ?: 0}"
                        )
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }
                Row(
                    modifier = Modifier
                        .alpha(alpha)
                        .padding(end = 24.dp)
                ) {
                    LabelValueRow(label = stringResource(id = R.string.time), value = time)
                }
            }

        }


        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (!isConfirmed) {
                DrugChip(label = stringResource(id = R.string.not_confirmed), textColor = redColor)
            }
            if (notExecuted) {
                DrugChip(label = stringResource(id = R.string.not_performed))
            }
            if (rejected) {
                DrugChip(label = stringResource(id = R.string.rejected_by_patient))
            }
        }
        Divider(Modifier.padding(top = 12.dp))
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
            quantity = 4.8,
            time = "10:30",
            isCompleted = false,
            rejected = true,
            isConfirmed = false,
            notExecuted = true,
            reserves = 2.0,
            hasDataToUpload = false
        ) {}
    }
}

@Composable
@Preview
private fun PreviewDrugAdministrationItemCompleted() {
    AppTheme() {
        DrugAdministrationItemView(
            name = "Meto Zeroch cpr ret 25mg",
            quantity = 4.0,
            time = "10:30",
            isCompleted = true,
            rejected = true,
            isConfirmed = false,
            notExecuted = true,
            reserves = 2.2,
            hasDataToUpload = true
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
            quantity = 4.4,
            time = "10:30",
            isCompleted = false,
            rejected = true,
            isConfirmed = false,
            notExecuted = true,
            isReserve = true,
            hasDataToUpload = true
        ) {}
    }
}