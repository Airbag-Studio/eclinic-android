package it.airbagstudio.ticare.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

fun Context.createImageFile(resized: Boolean = false): File {
    // Create an image file name
    //val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    //val imageFileName = "JPEG_" + timeStamp + "_" + if (resized) "thumb_" else ""
    val imageFileName = "test" + "_" + if (resized) "thumb_" else ""
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}