package com.example.looksy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MicrophoneButton() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        FloatingActionButton(
            onClick = { /* TODO: Aksi untuk Mic */ },
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            containerColor = Color(0xFFFFBB00)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Keep Press to Talk",
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFFFFFFF)
            )
        }
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center){
            Text(
                "Keep Press to Talk",
                modifier = Modifier.padding(bottom = 85.dp),
                color = Color.White
            )
        }
    }
}

