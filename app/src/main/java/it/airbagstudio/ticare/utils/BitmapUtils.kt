package it.airbagstudio.ticare.utils

import android.graphics.Bitmap

fun Bitmap.resized(maxSize: Int = 1000, compression: Int = 70): Bitmap {
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
    return Bitmap.createScaledBitmap(this, targetWidth, targetHeight, false)
}