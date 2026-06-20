package com.eddiapps.foodify.presentation.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun createCameraImageUri(context: Context): Pair<Uri, String> {
    val photoFile = File(context.filesDir, "meal_${UUID.randomUUID()}.jpg")
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
    return uri to photoFile.absolutePath
}

fun copyUriToAppStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val destFile = File(context.filesDir, "gallery_${UUID.randomUUID()}.jpg")
        FileOutputStream(destFile).use { output ->
            inputStream.use { input -> input.copyTo(output) }
        }
        destFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}