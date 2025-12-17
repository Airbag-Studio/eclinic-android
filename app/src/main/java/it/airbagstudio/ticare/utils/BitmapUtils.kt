package it.airbagstudio.ticare.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File


fun Bitmap.resized(maxSize: Int = 2000, compression: Int = 85): Bitmap {
    val sourceWidth: Int = this.width
    val sourceHeight: Int = this.height
    var targetWidth = maxSize
    var targetHeight = maxSize

    val sourceRatio = sourceWidth.toFloat() / sourceHeight.toFloat()
    val targetRatio = maxSize.toFloat() / maxSize.toFloat()

    if (targetRatio > sourceRatio) {
        targetWidth = (maxSize.toFloat() * sourceRatio).toInt()
    } else {
        targetHeight = (maxSize.toFloat() / sourceRatio).toInt()
    }
    return Bitmap.createScaledBitmap(this, targetWidth, targetHeight, true)
}

fun Bitmap.toByteArray(): ByteArray{
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    return stream.toByteArray()
}

fun Bitmap.correctOrientation(file: File): Bitmap {
    val exif = ExifInterface(file.absolutePath)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postRotate(270f)
            matrix.postScale(-1f, 1f)
        }
        else -> return this
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}