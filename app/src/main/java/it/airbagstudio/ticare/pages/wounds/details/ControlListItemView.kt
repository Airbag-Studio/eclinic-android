package it.airbagstudio.ticare.pages.wounds.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.theme.AppTheme

data class ControlListItem(
    val id: Int,
    val date: String,
    val description: String,
    val imagesCount: Int,
    val hasDataToUpload: Boolean
)

@Composable
fun ControlListItemView(item: ControlListItem,onClick:(Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(item.id)
            }
            .border(
                BorderStroke(1.dp, SolidColor(MaterialTheme.colorScheme.onSurfaceVariant)),
                RoundedCornerShape(12.dp)
            )
            .background(Color.White)
            .padding(16.dp)
    ) {
        if(item.hasDataToUpload){
            Column(Modifier.padding(end = 8.dp)) {
                Spacer(modifier = Modifier.height(2.dp))
                OfflineSyncImage(hasOfflineData = true, hasDataToSync = true)
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                item.date,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                item.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_photos),
                contentDescription = ""
            )
            Text(
                text = item.imagesCount.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
@Preview
private fun PreviewControlListItemView() {
    AppTheme {
        Scaffold {
            Column(
                Modifier
                    .padding(it)
                    .padding(16.dp)) {
                ControlListItemView(
                    item = ControlListItem(
                        id = 0,
                        date = "22/08/2023",
                        description = "Disinfettante e bendaggio.",
                        imagesCount = 5,
                        true
                    )
                ){}
                Spacer(modifier = Modifier.height(24.dp))
                ControlListItemView(
                    item = ControlListItem(
                        id = 0,
                        date = "22/08/2023",
                        description = "Disinfettante e bendaggio.",
                        imagesCount = 5,
                        false
                    )
                ){}
            }
        }

    }
}