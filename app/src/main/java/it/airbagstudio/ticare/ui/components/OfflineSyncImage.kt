package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun OfflineSyncImage(modifier: Modifier = Modifier,hasOfflineData: Boolean,hasDataToSync:Boolean){
    if (hasDataToSync){
        Icon(
            modifier = modifier.then(Modifier.size(18.dp)),
            painter = painterResource(id = R.drawable.ic_modified_data),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.error
        )
    }else if (hasOfflineData){
        Icon(
            modifier = modifier.then(Modifier.size(18.dp)),
            painter = painterResource(id = R.drawable.ic_patient_selected),
            contentDescription = "",
            tint = Color(0xFF24B400)

        )
    }
}

@Composable
@Preview
private fun PreviewOfflineSyncImage(){
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                OfflineSyncImage(modifier = Modifier.size(24.dp), hasOfflineData = true, hasDataToSync = true)
                OfflineSyncImage(modifier = Modifier.size(24.dp),hasOfflineData = true, hasDataToSync = false)
            }
        }
    }
}