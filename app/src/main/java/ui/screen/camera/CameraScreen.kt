package com.example.looksy.ui.screen.camera

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import com.example.looksy.data.camera.CameraXManager // Import CameraXManager
import com.example.looksy.ui.components.FavoriteButton
import com.example.looksy.ui.components.MicrophoneButton
import java.util.concurrent.Executors

@Composable
fun CameraScreen(isCameraPermissionGranted: Boolean) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }

    // Membuat instance CameraX Manager
    val cameraManager = remember { CameraXManager(context) }

    // Efek yang berjalan berdasarkan siklus hidup
    DisposableEffect(lifecycleOwner, isCameraPermissionGranted) {

        if (isCameraPermissionGranted) {
            // Meminta Manager untuk memulai kamera
            cameraManager.startCamera(
                lifecycleOwner,
                previewView,
                cameraExecutor
            )
        }

        onDispose {
            // Membersihkan saat Composable keluar (layar ditutup)
            cameraManager.stopCamera()
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Menampilkan Live Camera Feed
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay UI
        MicrophoneButton()
        FavoriteButton()
    }
}
