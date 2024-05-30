package it.airbagstudio.ticare.pages.tasksGeneric

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.AgendaTask
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.DrugChip
import it.airbagstudio.ticare.ui.components.LabelValueRow
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.checkGreen

data class TaskListItem(
    val name: String,
    val time: String,
    val executed:Boolean,
    val hasDataToUpload: Boolean,
    val task: AgendaTask
)

@Composable
fun TaskListItemView(item: TaskListItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 0.dp, bottom = 0.dp)
    ) {
        Row(Modifier.padding(end = 16.dp)) {
            if (item.executed || item.hasDataToUpload) {
                Column(modifier = Modifier.padding(end = 8.dp)) {
                    if (item.executed) {
                        Icon(
                            tint = checkGreen,
                            imageVector = Icons.Default.Check, contentDescription = ""
                        )
                    }
                    if (item.hasDataToUpload) {
                        Spacer(modifier = Modifier.padding(top = 2.dp))
                        OfflineSyncImage(hasOfflineData = true, hasDataToSync = true)
                    }
                }
            }
            Column {
                Row {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right_fill),
                        contentDescription = item.name
                    )
                }
                Row() {
                    LabelValueRow(
                        label = stringResource(id = R.string.time),
                        value = item.time
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (item.task.isSkipped) {
            DrugChip(label = stringResource(id = R.string.not_performed), includePadding = false)
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
    }
}