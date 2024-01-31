package it.airbagstudio.ticare.pages.patientsList.offlineDataSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.utils.format
import java.util.Date

@Composable
fun OfflineDataSheet(itemsToSync: Int, localItems: Int, isOffline: Boolean, expireDate: Date) {
   Column(modifier = Modifier .padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.padding(bottom = 24.dp)) {
            if (itemsToSync > 0) {
                DataToSyncChip(itemsToSync)
            } else {
                LocalDataChip(localItems)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isOffline) {
                OfflineDataChip()
            }
        }


        Text(
            text = stringResource(
                id = R.string.sync_data_expire_at,
                expireDate.format("dd/MM/yyyy HH:mm")
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(modifier = Modifier.padding(top = 8.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_help),
                contentDescription = "",
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(id = R.string.patient_data_expire_info),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
            )
        }

        Button(
            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = seed
            ),
            onClick = { /*TODO*/ }
        ) {
            Text(text = stringResource(id = R.string.work_with_local_data))
        }
    }
}

@Composable
private fun OfflineDataChip() {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Text(
            text = stringResource(id = R.string.only_local_data),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier.size(18.dp),
            painter = painterResource(id = R.drawable.ic_offline),
            contentDescription = ""
        )
    }
}

@Composable
private fun DataToSyncChip(items: Int) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(18.dp),
            painter = painterResource(id = R.drawable.ic_modified_data),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.patient_data, items.toInt()),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun LocalDataChip(items: Int) {
    Row(
        modifier = Modifier
            .background(color = Color(0xFFD1E7DD), shape = RoundedCornerShape(size = 12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(18.dp),
            painter = painterResource(id = R.drawable.ic_patient_selected),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.patient_data, "0"),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
@Preview(showSystemUi = true)
private fun PreviewOfflineDataSheet() {
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                OfflineDataSheet(
                    itemsToSync = 3,
                    localItems = 3,
                    isOffline = true,
                    Date()
                )
            }
        }
    }
}