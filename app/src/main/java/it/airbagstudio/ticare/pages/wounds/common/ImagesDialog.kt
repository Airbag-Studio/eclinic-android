package it.airbagstudio.ticare.pages.wounds.common

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Photo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.WoundPhoto
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import it.airbagstudio.ticare.BuildConfig
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ImageDetailsDialog
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.ui.components.okHttpClient
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesDialog(
    date: String,
   painters: List<AsyncImagePainter>,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    var selectedPainter by remember {
        mutableStateOf<Painter?>(null)
    }


    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismissRequest() }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
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

            painters.map {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            it.state.painter?.intrinsicSize
                                ?.let { intrinsicSize ->
                                    Modifier.aspectRatio(intrinsicSize.width / intrinsicSize.height)
                                } ?: Modifier.aspectRatio(4.0f / 3.0f)
                        )
                        .clickable {
                            selectedPainter = it
                        },
                    painter = it,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    if (selectedPainter != null){
        ImageDetailsDialog(painter = selectedPainter!!) {
            selectedPainter = null
        }
    }

}

@Composable
private fun getPainter(photo: WoundPhoto, requestData: ImageRequestData, imageLoader: ImageLoader, authTimestampHeader: String, isOnline: Boolean): AsyncImagePainter{
    val context = LocalContext.current
    if (isOnline){

        val url = "${requestData.url}/wounds/image?id=${photo.iD}"
        val imageRequest = ImageRequest.Builder(context)
            .data(url)
            .addHeader("Authorization", "Bearer ${requestData.token}")
            .addHeader("auth-timestamp", authTimestampHeader)
            .addHeader("api-version", BuildConfig.API_VERSION)
            .build()
        return rememberAsyncImagePainter(
            model = imageRequest,
            imageLoader = imageLoader,
            onError = {

            },
            onSuccess = {
                it.painter
            }
        )
    }else{
        val dir = context.getDir("images", Context.MODE_PRIVATE)
        val photoFile = File(dir, "${Uri.encode(photo.photo)}.jpg")
        return rememberAsyncImagePainter(model = photoFile)
    }

}