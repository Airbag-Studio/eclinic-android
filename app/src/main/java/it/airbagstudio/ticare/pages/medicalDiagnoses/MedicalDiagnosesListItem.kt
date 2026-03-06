package it.airbagstudio.ticare.pages.medicalDiagnoses

import android.R.attr.onClick
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.MedicalDiagnosis
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.LabelValueRow
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.theme.AppTheme

data class MedicalDiagnosesListItem(
    val openDate: String,
    val operatorName: String,
    val description: String,
    val hasDataToUpload: Boolean,
    val medicalDiagnosis: MedicalDiagnosis
)

@Composable
fun MedicalDiagnosesListItemView(item: MedicalDiagnosesListItem, onClick: () -> Unit){
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
                Column() {
                    LabelValueRow(
                        label = stringResource(id = R.string.user),
                        value = item.operatorName
                    )
                    LabelValueRow(
                        label = stringResource(id = R.string.time),
                        value = item.openDate
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = item.description
                )
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