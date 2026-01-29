package com.satwik.navigateai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.HouseSiding
import androidx.compose.material.icons.filled.School

// In DashboardUI.kt

@Composable
fun NextClassCard(
    modifier: Modifier = Modifier,
    subject: String = "Data Structures",
    room: String = "Room 304",
    time: String = "09:00 - 10:30",
    location: String = "Block 34 (CSE)",
    onClick: () -> Unit // <--- NEW: Accept a click function
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = onClick // <--- NEW: Trigger the function when card is tapped
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ... (Your existing UI content inside: Row, Text, Icons etc.) ...
            // (Keep the content exactly the same as before, just pasting the logic wrapper here)

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("NEXT CLASS", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text(subject, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("$room • $time", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, null, tint = Color(0xFFF44336), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(location, color = Color.LightGray, fontSize = 14.sp)
            }
        }
    }
}

// In DashboardUI.kt (At the bottom)

@Composable
fun QuickNavRow(
    onNavigateTo: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Quick Navigate",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 2. THE CHANGE: Use LazyRow instead of Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp), // Add breathing room at bottom
            contentPadding = PaddingValues(horizontal = 16.dp), // Space at start/end
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Space between buttons
        ) {
            // Item 1: Gate
            item {
                QuickNavButton(
                    name = "Main Gate",
                    icon = Icons.Default.DoorBack,
                    color = Color(0xFFF44336),
                    onClick = { onNavigateTo("gate_main") }
                )
            }

            // Item 2: Library
            item {
                QuickNavButton(
                    name = "Library",
                    icon = Icons.Default.MenuBook,
                    color = Color(0xFFFFC107),
                    onClick = { onNavigateTo("lib_central") }
                )
            }

            // Item 3: Canteen
            item {
                QuickNavButton(
                    name = "Canteen",
                    icon = Icons.Default.Restaurant,
                    color = Color(0xFFFF5722),
                    onClick = { onNavigateTo("unimall") }
                )
            }

            // Item 4: CSE Block (To prove it scrolls!)
            item {
                QuickNavButton(
                    name = "CSE Block",
                    icon = Icons.Default.School,
                    color = Color(0xFF4CAF50),
                    onClick = { onNavigateTo("block_34") }
                )
            }

            // Item 5: Hostels
            item {
                QuickNavButton(
                    name = "Hostels",
                    icon = Icons.Default.HouseSiding,
                    color = Color(0xFF9C27B0),
                    onClick = { onNavigateTo("gh_1") }
                )
            }
        }
    }
}

// Helper Composable for the individual buttons
@Composable
fun QuickNavButton(
    name: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)), // Dark Blue
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(start = 16.dp)
            .height(50.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, color = Color.White)
    }
}
