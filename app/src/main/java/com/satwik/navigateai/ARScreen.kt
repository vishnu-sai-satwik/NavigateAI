package com.satwik.navigateai

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Composable
fun ARScreen(
    onClose: () -> Unit // Function to go back to Map
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. THE CAMERA FEED
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (e: Exception) {
                        Log.e("ARScreen", "Use case binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        // 2. THE AR OVERLAY (The "Hype")
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val cy = h / 2

            // Draw Crosshairs (Sci-Fi style)
            drawLine(Color.White.copy(alpha=0.5f), Offset(cx - 50, cy), Offset(cx + 50, cy), 2f)
            drawLine(Color.White.copy(alpha=0.5f), Offset(cx, cy - 50), Offset(cx, cy + 50), 2f)

            // Draw The Navigation Arrow (Pointing Forward)
            val arrowPath = Path().apply {
                moveTo(cx, cy - 150) // Tip
                lineTo(cx - 40, cy - 50) // Bottom Left
                lineTo(cx, cy - 80)      // Inner notch
                lineTo(cx + 40, cy - 50) // Bottom Right
                close()
            }
            drawPath(arrowPath, Color(0xFF00E676)) // Neon Green Fill
            drawPath(arrowPath, color = Color.White, style = Stroke(width = 5f)) // White Border
        }

        // 3. HUD (Heads Up Display) Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp), // Move up above nav bar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "50m",
                color = Color(0xFF00E676),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "to Central Library",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.background(Color.Black.copy(alpha=0.5f), RoundedCornerShape(4.dp)).padding(4.dp)
            )
        }

        // 4. Close Button (Top Right)
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(32.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close AR", tint = Color.White)
        }
    }
}