package com.example.looksy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.looksy.ui.screen.camera.CameraScreen

import com.example.looksy.ui.theme.LooksyTheme


class MainActivity : ComponentActivity() {

    // Melacak izin kamera
    var isCameraPermissionGranted by mutableStateOf(false)

    // meminta izin
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isCameraPermissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(this, "Izin kamera ditolak.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCameraPermission()

        setContent {
            LooksyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraScreen(
                        isCameraPermissionGranted = isCameraPermissionGranted
                    )
                }
            }
        }
    }

    // Fungsi untuk memeriksa dan meminta izin
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            isCameraPermissionGranted = true
        }
    }
}