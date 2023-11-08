package it.airbagstudio.ticare.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import it.airbagstudio.ticare.R
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.time.Instant
import java.time.format.DateTimeFormatter

data class ImageRequestData(
    val url: String,
    val token: String
)

val okHttpClient = OkHttpClient.Builder()
    .dispatcher(Dispatcher().apply {
        maxRequests = 2
    })
    .build()

@Composable
fun PatientImage(code: String, photo: String, requestData: ImageRequestData) {
    Box(
        modifier = Modifier
            .width(56.dp)
            .height(56.dp)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_person),
            contentDescription = "",
            modifier = Modifier.padding(4.dp)
        )

        if (photo.isNotBlank()) {
            val url = "${requestData.url}/cases/case/image?cod=${Uri.encode(code)}&photo=${Uri.encode(photo)}"
            val authTimestampHeader = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            val imageRequest = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .addHeader("Authorization", "Bearer ${requestData.token}")
                .addHeader("auth-timestamp", authTimestampHeader)
                .build()
            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .okHttpClient(okHttpClient)
                .build()
            Image(
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(4.dp)),
                painter = rememberAsyncImagePainter(
                    model = imageRequest,
                    imageLoader = imageLoader
                ),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }
    }
}
