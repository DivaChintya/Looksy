package com.example.looksy.data.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    val context: Context,
    val detectorListener: DetectorListener?
) {

    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    private fun setupObjectDetector() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(0.5f) // Hanya ambil benda yang kemiripannya > 50%
            .setMaxResults(3) // Maksimal 3 benda agar tidak berat

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(2)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        // Nama file harus sama dengan yang ada di folder assets
        val modelName = "mobilenet_v1.tflite"

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context, modelName, optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            detectorListener?.onError("Object detector failed to initialize. See error logs for details")
        }
    }

    fun detect(image: Bitmap, rotation: Int) {
        if (objectDetector == null) {
            setupObjectDetector()
        }

        var inferenceTime = SystemClock.uptimeMillis()

        // Proses gambar agar sesuai dengan input AI
        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-rotation / 90))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        // Jalankan deteksi
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        // Kirim hasil ke UI melalui Listener
        detectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }
}