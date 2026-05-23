package com.eddiapps.foodify.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    val scaled = bitmap.scale(1024, 1024)
    scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}

fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String {
    val maxSize = 512
    val ratio = minOf(
        maxSize.toFloat() / bitmap.width,
        maxSize.toFloat() / bitmap.height
    )
    val scaled = if (ratio < 1f) {
        bitmap.scale((bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt())
    } else bitmap

    val fileName = "meal_${UUID.randomUUID()}.jpg"
    val file = File(context.filesDir, fileName)

    FileOutputStream(file).use { out ->
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
    }

    return file.absolutePath
}