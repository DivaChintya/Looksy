package com.example.looksy.data.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService

// Class ini mengelola semua logika CameraX (Kamera data layer)
class CameraXManager(private val context: Context) {

    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        cameraExecutor: ExecutorService
    ) {
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // 1. Preview Use Case: Menghubungkan output kamera ke PreviewView (Surface)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                // 2. Bind: Menghubungkan use case Preview ke siklus hidup (lifecycle)
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                    // TODO: Minggu 2: ImageAnalysis akan ditambahkan di sini
                )
            } catch(exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Fungsi untuk membersihkan sumber daya kamera
    fun stopCamera() {
        // Unbind semua use case
        cameraProviderFuture.get().unbindAll()
    }
}
