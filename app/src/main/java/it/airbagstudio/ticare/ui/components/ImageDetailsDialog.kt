package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import me.saket.telephoto.zoomable.ZoomableContentLocation
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun ImageDetailsDialog(
    painter: Painter,
    onDismissRequest: () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
            val state = rememberZoomableState()
            LaunchedEffect(painter.intrinsicSize) {
                state.setContentLocation(
                    ZoomableContentLocation.scaledInsideAndCenterAligned(painter.intrinsicSize)
                )
            }
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .zoomable(state),
                painter = painter,
                contentDescription = "",
                contentScale = ContentScale.Inside,
                alignment = Alignment.Center,
            )
            IconButton(onClick = {
                onDismissRequest()
            }) {
                Image(painter = painterResource(id = R.drawable.ic_cancel), contentDescription = "")
            }
        }

    }
}

@Composable
@Preview
private fun PreviewImageDetailsDialog() {
    var showDialog by remember {
        mutableStateOf(false)
    }
    val painter = rememberAsyncImagePainter(
        model = "https://www.seiu1000.org/sites/main/files/main-images/camera_lense_0.jpeg"
    )
    AppTheme {
        Image(painter = painter, contentDescription = "", modifier = Modifier.clickable {
            showDialog = true
        })
        if (showDialog) {
            ImageDetailsDialog(painter) {
                showDialog = false
            }
        }
    }
}