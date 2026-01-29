package com.satwik.navigateai

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.hypot
@Composable
fun MapScreen() {
    // 1. Get our LPU data
    val nodes = CampusData.nodes
    val edges = CampusData.edges
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // 1. STATE: To hold the "Next Class" info
    var subject by remember { mutableStateOf("No Class Scheduled") }
    var room by remember { mutableStateOf("Free Period") }
    var time by remember { mutableStateOf("--:--") }
    var locationName by remember { mutableStateOf("Relax") }
    var isLoading by remember { mutableStateOf(false) }
    // State for Navigation
    var startNodeId by remember { mutableStateOf<String?>(null) }
    var endNodeId by remember { mutableStateOf<String?>(null) }
    var currentPath by remember { mutableStateOf<List<String>>(emptyList()) }
    // 2. State for Zooming and Panning (To make it feel like a real map)
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    // 2. THE CAMERA LAUNCHER (Opens System Camera)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            isLoading = true
            Toast.makeText(context, "AI is analyzing timetable...", Toast.LENGTH_LONG).show()

            // 3. CALL GEMINI (Background Thread)
            scope.launch {
                val jsonResult = GeminiHelper.analyzeTimetable(bitmap)

                // Parse the JSON response
                try {
                    // Gemini returns a JSON string, we clean it just in case
                    val cleanJson = jsonResult.replace("```json", "").replace("```", "").trim()
                    val jsonObj = JSONObject(cleanJson)

                    // Update the UI!
                    subject = jsonObj.getString("subject")
                    room = jsonObj.getString("room")
                    time = jsonObj.getString("time")
                    locationName = "Detected from Timetable"

                    // Auto-Navigate Logic (Optional)
                    // If room is "304", we could auto-set endNodeId = "block_34" here!

                } catch (e: Exception) {
                    Toast.makeText(context, "AI couldn't read that. Try again.", Toast.LENGTH_SHORT).show()
                }
                isLoading = false
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += pan
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    // 1. MATH: Convert the tap pixels back to "Map Coordinates"
                    val mapSize = 1000f
                    val scaleFactor = (size.width / mapSize) * scale
                    val centerOffsetX = (size.width - (mapSize * scaleFactor)) / 2 + offset.x
                    val centerOffsetY = (size.height - (mapSize * scaleFactor)) / 2 + offset.y

                    // 2. Find which node was clicked (Hitbox check)
                    val clickedNode = nodes.find { node ->
                        val nodeX = node.x * scaleFactor + centerOffsetX
                        val nodeY = node.y * scaleFactor + centerOffsetY
                        // Check if tap is within 50 pixels of the node
                        hypot(tapOffset.x - nodeX, tapOffset.y - nodeY) < 50f
                    }

                    // 3. Handle Selection Logic
                    if (clickedNode != null) {
                        if (startNodeId == null) {
                            startNodeId = clickedNode.id
                        } else if (endNodeId == null) {
                            endNodeId = clickedNode.id
                            // TRIGGER THE BRAIN: Calculate Path!
                            currentPath = PathFinder.findShortestPath(
                                startNodeId!!, endNodeId!!, nodes, edges
                            )
                        } else {
                            // Reset if clicking again
                            startNodeId = clickedNode.id
                            endNodeId = null
                            currentPath = emptyList()
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) { // Apply Zoom & Pan. We use specific math to center the 1000x1000 grid on your phone screen
            val mapSize = 1000f
            val scaleFactor = (size.width / mapSize) * scale
            val centerOffsetX = (size.width - (mapSize * scaleFactor)) / 2 + offset.x
            val centerOffsetY = (size.height - (mapSize * scaleFactor)) / 2 + offset.y
            // 3. Draw Edges (The Paths) - Gray Lines
            edges.forEach { edge ->
                val start = nodes.find { it.id == edge.fromId }!!
                val end = nodes.find { it.id == edge.toId}!!
                drawLine(
                    color = Color.Gray.copy(alpha = 0.3f),
                    start = Offset(x = start.x * scaleFactor + centerOffsetX, y = start.y * scaleFactor + centerOffsetY),
                     end = Offset(x = end.x * scaleFactor + centerOffsetX, y = end.y * scaleFactor + centerOffsetY),
                    strokeWidth = 3f * scale,
//                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }
            // --- LAYER 2: The "Active" Neon Path ---
            if (currentPath.isNotEmpty()) {
                for (i in 0 until currentPath.size - 1) {
                    val u = nodes.find { it.id == currentPath[i] }!!
                    val v = nodes.find { it.id == currentPath[i+1] }!!
                    drawLine(
                        color = Color(0xFF00FF00), // NEON GREEN
                        start = Offset(u.x * scaleFactor + centerOffsetX, u.y * scaleFactor + centerOffsetY),
                        end = Offset(v.x * scaleFactor + centerOffsetX, v.y * scaleFactor + centerOffsetY),
                        strokeWidth = 8f * scale,
                        cap = StrokeCap.Round
                    )
                }
            }
            // 4. Draw Nodes (The Buildings) - Colored Circles
            nodes.forEach { node ->
                val nodeX = node.x * scaleFactor + centerOffsetX
                val nodeY = node.y * scaleFactor + centerOffsetY
                // Color Logic: Green for Start, Red for End, Node Color for others
                val nodeColor = when (node.id) {
                    startNodeId -> Color.Green
                    endNodeId -> Color.Red
                    else -> node.type.color
                }
                drawCircle(
                    color = node.type.color,
                    radius = 12f * scale,
                    center = Offset(nodeX, nodeY)
                )
                // Optional: Draw 'Halo' for selected nodes
                if (node.id == startNodeId || node.id == endNodeId) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.3f),
                        radius = 25f * scale,
                        center = Offset(nodeX, nodeY)
                    )
                }
            }
        }
        // --- 2. The Top Overlay (Updated with SCAN Button) ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
                .padding(horizontal = 16.dp)
        ) {
            // THE NEXT CLASS CARD (Dynamic Data!)
            NextClassCard(
                subject = if (isLoading) "Analyzing..." else subject,
                room = room,
                time = time,
                location = locationName,
                onClick = { /* Navigate logic */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // THE SCAN BUTTON (New!)
            Button(
                onClick = { cameraLauncher.launch() }, // Launches System Camera
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)), // Deep Purple
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("📷 Scan Timetable")
            }
        }
        // --- UI OVERLAY: Instructions ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp) // Lift it up a bit
        ) {
            QuickNavRow(
                onNavigateTo = { destinationId ->
                    // LOGIC: When button is clicked...
                    // 1. Set Start Node to "Main Gate" (or Current Location later)
                    startNodeId = "gate_main" // For Demo: Assume we start at Gate

                    // 2. Set End Node to the button clicked
                    endNodeId = destinationId

                    // 3. Trigger Calculation
                    currentPath = PathFinder.findShortestPath(
                        startNodeId!!, endNodeId!!, nodes, edges
                    )
                }
            )
        }
    }

























}