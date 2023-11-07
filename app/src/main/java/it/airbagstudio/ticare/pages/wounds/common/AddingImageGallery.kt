package it.airbagstudio.ticare.pages.wounds.common

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import it.airbagstudio.ticare.R

@Composable
fun AddingImagesGallery(images: List<Bitmap>, onDelete: (Bitmap) -> Unit) {
    LazyHorizontalGrid(
        modifier = Modifier.height(128.dp),
        rows = GridCells.FixedSize(128.dp), content = {
            items(images) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(
                                RoundedCornerShape(24.dp)
                            ),
                        contentScale = ContentScale.Crop,
                        painter = rememberAsyncImagePainter(
                            model = it,
                            contentScale = ContentScale.Crop
                        ),
                        contentDescription = ""
                    )
                    Image(
                        modifier = Modifier.clickable {
                            onDelete(it)
                        },
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = ""
                    )
                }
            }
        })
}