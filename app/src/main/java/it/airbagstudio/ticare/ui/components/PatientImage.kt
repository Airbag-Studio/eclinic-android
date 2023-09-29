package it.airbagstudio.ticare.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import java.util.Base64

@Composable
fun PatientImage(base64Image: String?) {
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
        if (base64Image != null) {
            getBitmap(base64Image = base64Image)?.let { bitmap ->
                Image(
                    modifier = Modifier
                        .width(56.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

private fun getBitmap(base64Image: String?): Bitmap? {
    try {
        val decoded = Base64.getDecoder().decode(base64Image)
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.count())
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return null
}