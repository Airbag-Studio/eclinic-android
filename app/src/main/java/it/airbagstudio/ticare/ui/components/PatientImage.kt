package it.airbagstudio.ticare.ui.components

import android.content.Context
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
import androidx.compose.ui.graphics.painter.Painter
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
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

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
fun PatientImage(code: String, photo: String, requestData: ImageRequestData,isOnline: Boolean = true) {

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
            Image(
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(4.dp)),
                painter = getPainter(code,photo,requestData,isOnline),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }
    }



}
@Composable
private fun getPainter(code: String, photo: String, requestData: ImageRequestData,isOnline: Boolean): Painter{
    val context = LocalContext.current
    if (isOnline){
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
        return rememberAsyncImagePainter(
                model = imageRequest,
        imageLoader = imageLoader
        )
    }else{
        val dir = context.getDir("images", Context.MODE_PRIVATE)
        val photoFile = File(dir, Uri.encode(photo))
        return rememberAsyncImagePainter(model = photoFile)
    }

}