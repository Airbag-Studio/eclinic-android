package it.airbagstudio.ticare.pages.nursingCourses

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.components.shimmerBrush
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun NursingCourseItemView(
    name: String,
    time: String,
    duration: Int?,
    description: String,
    hasDataToUpload: Boolean,
    onClick: () -> Unit
) {
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
                    if( hasDataToUpload) {
                        OfflineSyncImage(hasOfflineData = false, hasDataToSync = hasDataToUpload)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = name
                    )
                }
                Row(
                    modifier = Modifier.padding(end = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LabelValueRow(label = stringResource(id = R.string.duration), value = "$duration")
                    Spacer(modifier = Modifier.width(8.dp))
                    LabelValueRow(
                        label = stringResource(id = R.string.time),
                        value = time
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
                        text = description,
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



        Divider(modifier = Modifier.padding(top = 8.dp))

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

@Composable
@Preview
fun NursingCoursesItemViewLoading() {
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
private fun PreviewNursingCourseItemView() {
    AppTheme {
        NursingCourseItemView(
            name = "Giovanna Verdi",
            duration = 4,
            time = "10:30",
            description = "Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            hasDataToUpload = true
        ) {}
    }
}