package com.satwik.navigateai

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
// 1. The Node: Represents a physical place on your campus map.
data class CampusNode (
    val id: String, // Unique ID (e.g., "lib_main")
    val name: String, // Display Name (e.g., "Central Library")
    val x: Float, // X Position (0f to 1000f) - Relative to screen width
    val y: Float, // Y Position (0f to 1000f) - Relative to screen height
    val type: NodeType // What kind of building is it?
)
// 2. The Node Type: Defines the color and icon for each building.
enum class NodeType(val color: Color, val icon: ImageVector ) {
    CLASSROOM(Color(0xFF4CAF50), Icons.Default.School), // Green
    LAB(Color(0xFF2196F3), Icons.Default.Science), // Blue
    LIBRARY(Color(0xFFFFC107), Icons.AutoMirrored.Filled.MenuBook), // Amber
    CANTEEN(Color(0xFFFF5722), Icons.Default.Restaurant), // Orange
    HOSTEL(Color(0xFF9C27B0), Icons.Default.Bed), // Purple
    GATE(Color(0xFFF44336), Icons.Default.DoorBack), // Red
    MALL(Color(0xFF673AB7), Icons.Default.LocalMall),
    AUDITORIUM(Color(0xFF673AB7), Icons.Default.EventSeat)
}
// 3. The Edge: Represents a walkable path between two nodes.
data class PathEdge(
    val fromId: String, // Start Node ID
    val toId: String, // End Node ID
    val distance: Int // Distance in meters (Used for Dijkstra calculation)
)