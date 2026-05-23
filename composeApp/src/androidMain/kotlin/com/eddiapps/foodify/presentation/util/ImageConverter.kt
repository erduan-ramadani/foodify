package com.eddiapps.foodify.presentation.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

fun imageFileToBase64(filePath: String): String {
    val bitmap = BitmapFactory.decodeFile(filePath)

    // Verkleinern für API
    val maxSize = 1024
    val ratio = minOf(
        maxSize.toFloat() / bitmap.width,
        maxSize.toFloat() / bitmap.height
    )
    val scaled = if (ratio < 1f) {
        bitmap.scale((bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt())
    } else bitmap

    val outputStream = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}