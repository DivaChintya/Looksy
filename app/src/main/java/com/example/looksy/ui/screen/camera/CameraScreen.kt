package com.example.looksy.ui.screen.camera

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.example.looksy.data.camera.CameraXManager
import com.example.looksy.ui.components.CameraButton
import com.example.looksy.ui.components.MicrophoneButton
import java.util.concurrent.Executors

@Composable
fun CameraScreen(isCameraPermissionGranted: Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Gunakan remember agar executor tidak dibuat ulang setiap kali recompose
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            // Memastikan resource dibersihkan saat layar ditutup
            cameraManager.stopCamera()
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isCameraPermissionGranted) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        cameraManager.startCamera(lifecycleOwner, this)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        MicrophoneButton()
        CameraButton(onCaptureClick = {
            cameraManager.takePhoto()
        })
    }
}