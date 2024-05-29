package it.airbagstudio.ticare.pages.coursesGeneric

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.HomeCareCourse
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.LabelValueRow
import it.airbagstudio.ticare.ui.components.OfflineSyncImage

data class CourseListItem(
    val name: String,
    val time: String,
    val duration: Int?,
    val description: String,
    val hasDataToUpload: Boolean,
    val course: HomeCareCourse
)

@Composable
fun CourseListItemView(item: CourseListItem,onClick: () -> Unit){
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp)
    ) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, bottom = 0.dp)
        ) {

            Row(modifier = Modifier.padding(end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if( item.hasDataToUpload) {
                    OfflineSyncImage(hasOfflineData = false, hasDataToSync = item.hasDataToUpload)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = item.name
                )
            }
            Row(
                modifier = Modifier.padding(end = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                LabelValueRow(label = stringResource(id = R.string.duration), value = "${item.duration}")
                Spacer(modifier = Modifier.width(8.dp))
                LabelValueRow(
                    label = stringResource(id = R.string.time),
                    value = item.time
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(end = 24.dp, top = 8.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
            ) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

        }



        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

    }
}

@Composable
private fun LabelValueRow(label: String, value: String) {
    Row {
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