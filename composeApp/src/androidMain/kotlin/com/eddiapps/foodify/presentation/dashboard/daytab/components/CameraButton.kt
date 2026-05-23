package com.eddiapps.foodify.presentation.dashboard.daytab.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.eddiapps.foodify.R
import com.eddiapps.foodify.presentation.util.bitmapToBase64
import com.eddiapps.foodify.presentation.util.saveBitmapToInternalStorage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraButton(
    onCameraClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val imagePath = saveBitmapToInternalStorage(context, it)
            val base64 = bitmapToBase64(it)
            onCameraClick(base64, imagePath)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    IconButton(
        onClick = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                cameraLauncher.launch(null)
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