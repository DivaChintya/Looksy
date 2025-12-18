package com.example.looksy.data.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Build
import android.widget.Toast

import android.util.Log

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

    private var imageCapture: ImageCapture? = null

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 1. Inisialisasi Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // 2. Inisialisasi ImageCapture (Tugas Baru)
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                // 3. Bind Preview dan ImageCapture ke Lifecycle
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // 4. Fungsi untuk mengambil foto
    fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Buat nama file unik berdasarkan waktu
        val name = "Looksy_${System.currentTimeMillis()}.jpg"

        // Konfigurasi MediaStore untuk simpan ke galeri
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Looksy-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(context, "Foto berhasil disimpan ke Galeri!", Toast.LENGTH_SHORT).show()
                }
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraX", "Gagal mengambil foto: ${exc.message}", exc)
                }
            }
        )
    }

}
