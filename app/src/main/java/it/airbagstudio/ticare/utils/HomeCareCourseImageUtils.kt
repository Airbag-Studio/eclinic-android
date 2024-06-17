package it.airbagstudio.ticare.utils

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ch.ticare.eclinic.library.entity.HomeCareCourseImage
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import it.airbagstudio.ticare.BuildConfig
import it.airbagstudio.ticare.ui.components.ImageRequestData
import java.io.File

@Composable
fun HomeCareCourseImage.getPainter(
    requestData: ImageRequestData,
    imageLoader: ImageLoader,
    authTimestampHeader: String,
    isOnline: Boolean,
    tool: String
): AsyncImagePainter {
    val context = LocalContext.current
    if (isOnline) {

        val url = "${requestData.url}/courses/image?t=${tool}&id=${this.id}"
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
    } else {
        val dir = context.getDir("images", Context.MODE_PRIVATE)
        val photoFile = File(dir, "${Uri.encode(this.photo)}.jpg")
        return rememberAsyncImagePainter(model = photoFile)
    }

}