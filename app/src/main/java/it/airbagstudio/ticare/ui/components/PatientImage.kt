package it.airbagstudio.ticare.ui.components

import android.net.Uri
import android.util.Log
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import it.airbagstudio.ticare.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PatientImageRequestData(
    val url: String,
    val token: String
)

@Composable
fun PatientImage(code: String, photo: String, requestData: PatientImageRequestData) {
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
            Image(
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(4.dp)),
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(url)
                    .addHeader("Authorization", "Bearer ${requestData.token}")
                    .addHeader("auth-timestamp", authTimestampHeader)
                    .build()),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }
    }
}
