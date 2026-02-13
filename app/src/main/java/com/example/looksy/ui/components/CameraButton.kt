package com.example.looksy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraButton(onCaptureClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, bottom = 28.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        FloatingActionButton(
            onClick = { onCaptureClick() },
            modifier = Modifier.size(56.dp),
            containerColor = Color(0xCCFFFFFF)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Capture",
                modifier = Modifier.size(35.dp),
                tint = Color(0xFF595757)
            )
        }
    }
}
