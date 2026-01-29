package com.satwik.navigateai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.satwik.navigateai.ui.theme.NavigateAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) { /* Good to go */ }
        }
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        enableEdgeToEdge()
        setContent {
            NavigateAITheme {
                var showAR by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A) // Midnight Blue
                ) {
                    if (showAR) {
                        // SHOW AR CAMERA
                        ARScreen(onClose = { showAR = false })
                    } else {
                        // SHOW MAP
                        Box(modifier = Modifier.fillMaxSize()) {
                            MapScreen() // Your existing Map

                            // THE AR BUTTON (Top Right)
                            Button(
                                onClick = { showAR = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B6F6)), // Cyber Blue
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 48.dp, end = 16.dp)
                            ) {
                                Text("AR MODE")
                            }
                        }
                    }
                }
            }
        }
    }
}