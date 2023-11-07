package it.airbagstudio.ticare.pages.wounds.common

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ImageRequestData
import java.time.Instant
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesDialog(
    date: String,
    photosIds: List<Int>,
    requestData: ImageRequestData,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismissRequest() }) {
        Column(modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.photo),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            photosIds.forEach { photoId ->
                val url = "${requestData.url}/wounds/image?id=${photoId}"
                val authTimestampHeader = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                val painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(url)
                        .addHeader("Authorization", "Bearer ${requestData.token}")
                        .addHeader("auth-timestamp", authTimestampHeader)
                        .build(),
                )

                Image(
                    modifier = Modifier.fillMaxWidth().then(
                        painter.state.painter?.intrinsicSize
                            ?.let { intrinsicSize ->
                                Modifier.aspectRatio(intrinsicSize.width / intrinsicSize.height)
                            } ?: Modifier.aspectRatio(4.0f / 3.0f)
                    ),
                    painter = painter,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}