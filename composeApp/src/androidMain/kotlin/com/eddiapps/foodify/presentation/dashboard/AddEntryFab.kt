package com.eddiapps.foodify.presentation.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.eddiapps.foodify.presentation.dashboard.daytab.components.TextInputSheet
import com.eddiapps.foodify.presentation.dashboard.daytab.components.VoiceInputSheet
import com.eddiapps.foodify.presentation.util.copyUriToAppStorage
import com.eddiapps.foodify.presentation.util.createCameraImageUri

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEntryFab(
    onTextClick: (String) -> Unit,
    onCameraClick: (String) -> Unit,
    onGalleryClick: (String) -> Unit,
    onMicClick: (String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showVoiceSheet by remember { mutableStateOf(false) }
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var showTextSheet by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val savedPath = copyUriToAppStorage(context, uri)
        if (savedPath != null) {
            onGalleryClick(savedPath)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            currentPhotoPath?.let {
                onCameraClick(it)
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val (uri, path) = createCameraImageUri(context)
            currentPhotoPath = path
            cameraLauncher.launch(uri)
        }
    }

    Column(
        modifier.padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(visible = isExpanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallFloatingActionButton(onClick = {
                    isExpanded = false
                    showTextSheet = true
                }) {
                    Icon(Icons.Default.Keyboard, contentDescription = "Text")
                }
                SmallFloatingActionButton(onClick = {
                    isExpanded = false
                    showVoiceSheet = true
                }) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice")
                }
                SmallFloatingActionButton(onClick = {
                    isExpanded = false
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        val (uri, path) = createCameraImageUri(context)
                        currentPhotoPath = path
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Camera")
                }

                SmallFloatingActionButton(onClick = {
                    isExpanded = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                }
            }
        }

        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = if (isLoading)
                MaterialTheme.colorScheme.surfaceVariant
            else
                FloatingActionButtonDefaults.containerColor
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (isExpanded) "Close" else "Open"
                )
            }
        }
    }

    if (showVoiceSheet) {
        VoiceInputSheet(
            onDismiss = { showVoiceSheet = false },
            onTextRecognized = { spokenText ->
                onMicClick(spokenText)
            }
        )
    }

    if (showTextSheet) {
        TextInputSheet(
            onDismiss = { showTextSheet = false },
            onSend = { text ->
                showTextSheet = false
                onTextClick(text)
            }
        )
    }
}