package com.eddiapps.foodify.presentation.dashboard.daytab.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.eddiapps.foodify.R
import java.io.File
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraButton(
    onCameraClick: (String) -> Unit
) {
    val context = LocalContext.current
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            currentPhotoPath?.let { onCameraClick(it) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera(context) { uri, path ->
                currentPhotoPath = path
                cameraLauncher.launch(uri)
            }
        }
    }

    IconButton(
        onClick = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                launchCamera(context) { uri, path ->
                    currentPhotoPath = path
                    cameraLauncher.launch(uri)
                }
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = stringResource(R.string.take_photo),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun launchCamera(
    context: android.content.Context,
    onReady: (Uri, String) -> Unit
) {
    val photoFile = File(context.filesDir, "meal_${UUID.randomUUID()}.jpg")
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
    onReady(uri, photoFile.absolutePath)
}