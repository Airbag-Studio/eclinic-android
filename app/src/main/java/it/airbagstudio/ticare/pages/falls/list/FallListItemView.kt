package it.airbagstudio.ticare.pages.falls.list

import android.R.attr.fontWeight
import android.R.attr.maxLines
import android.R.attr.onClick
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.OfflineSyncImage

@Composable
fun FallListItemView(item: FallListItem,onClick: (Int) -> Unit){
    Row(modifier = Modifier
        .clickable {
            onClick(item.id)
        }
        .padding(start = 16.dp, top = 12.dp, 24.dp, 12.dp)) {
        if (item.hasDataToUpload){
            Column {
                Spacer(modifier = Modifier.height(4.dp))
                OfflineSyncImage(hasOfflineData = true, hasDataToSync = true)
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            item.info.keys.forEach{ key ->
                Text(
                    text = item.info.getValue(key),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = item.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")

    }
}