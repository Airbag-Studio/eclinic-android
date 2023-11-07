package it.airbagstudio.ticare.ui.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R

data class ImageRequestData(
    val url: String,
    val token: String
)

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
        /*
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

         */
    }
}
