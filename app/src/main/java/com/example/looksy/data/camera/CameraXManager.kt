package com.example.looksy.data.camera

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import android.graphics.Bitmap
import org.tensorflow.lite.task.vision.detector.Detection

class CameraXManager(private val context: Context) {

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var objectDetectorHelper: ObjectDetectorHelper? = null //week 2
    private var imageAnalyzer: ImageAnalysis? = null //Week 2

    // HANYA GUNAKAN SATU FUNGSI INI. HAPUS YANG LAINNYA.
    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                // Preview Config
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // ImageCapture Config
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                // Inisialisasi listener untuk menerima hasil deteksi
                val detectorListener = object : ObjectDetectorHelper.DetectorListener {
                    override fun onError(error: String) {
                        Log.e("LooksyAI", "Error: $error")
                    }

                    override fun onResults(results: MutableList<Detection>?, inferenceTime: Long, imageHeight: Int, imageWidth: Int) {
                        // Logika untuk menampilkan koordinat atau menggambar mata di sini
                        results?.forEach {
                            Log.d("LooksyAI", "Objek ditemukan: ${it.categories.firstOrNull()?.label}")
                        }
                    }
                }

                objectDetectorHelper = ObjectDetectorHelper(context, detectorListener)

        // Tambahkan Analyzer
        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    // Konversi imageProxy ke Bitmap dan kirim ke AI
                    try {
                        val bitmapBuffer = Bitmap.createBitmap(
                            imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
                        )
                        // Mengambil buffer dari plane pertama (RGBA)
                        bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)

                        objectDetectorHelper?.detect(
                            bitmapBuffer,
                            imageProxy.imageInfo.rotationDegrees
                        )
                    } catch (e: Exception) {
                        Log.e("CameraX", "Analisis gagal: ${e.message}")
                    } finally {
                        // ImageProxy HARUS ditutup di sini agar frame berikutnya bisa masuk
                        imageProxy.close()
                    }
                }
            }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer //untuk deteksi AI
                )

            } catch (exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))

    }

    fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = "Looksy_${System.currentTimeMillis()}.jpg"
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
                    Toast.makeText(context, "Foto berhasil disimpan!", Toast.LENGTH_SHORT).show()
                }
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraX", "Gagal: ${exc.message}", exc)
                }
            }
        )
    }

    fun stopCamera() {
        cameraProvider?.unbindAll()
    }
}