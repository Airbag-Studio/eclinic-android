package it.airbagstudio.ticare.pages.carePlans.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.theme.AppTheme

data class CarePlanesListItem(
    val title: String,
    val date:String,
    val id: Int,
    val hasDataToUpload: Boolean
)

@Composable
internal fun CarePlanesListItemView(item: CarePlanesListItem,onClick: (Int) -> Unit){
    Row(modifier = Modifier
        .clickable {
            onClick(item.id)
        }
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (item.hasDataToUpload){
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                OfflineSyncImage(hasOfflineData = true, hasDataToSync = true)
                Spacer(modifier = Modifier.height(20.dp))
            }

        }
        Column(
            Modifier
                .weight(1f)
                .padding(end = 16.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Image(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = item.title)
    }
}

@Composable
@Preview
private fun CarePlanesListItemViewPreview(){
    AppTheme {
        CarePlanesListItemView(CarePlanesListItem("Rischio di cadute","12/09/2021",1,true)){}
    }
}