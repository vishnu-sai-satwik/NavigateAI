# NaviGate AI - Intelligent Campus Navigation System 📍🤖
# Overview
NaviGate AI is a native Android application engineered to solve the "last-mile" navigation problem within complex university campuses. Unlike standard map apps that stop at the building entrance, NaviGate AI provides indoor, graph-based pathfinding and AI-powered scheduling to guide students specifically to their classrooms and labs.

Project Status: MVP / Technical Prototype

# Focus: Android Engineering, Data Structures & Algorithms (DSA), and Generative AI Integration.

# 🚀 Key Technical Features
Custom Graph Navigation Engine (DSA):

Implemented Dijkstra’s Shortest Path Algorithm from scratch (no external map SDK wrappers) to calculate optimal routes between building nodes.

Demonstrates practical application of Graph Theory and Weighted Edges in a real-world mobile context.

AI-Powered Schedule Parsing:

Integrated Google Gemini 1.5 Pro API via Retrofit.

# Feature: Users can scan a physical/printed timetable; the app extracts class details (Subject, Room, Time) using Multimodal AI and auto-configures navigation destinations.

# AR-Style Sensor Fusion:

Utilizes Android CameraX and hardware sensors (Magnetometer & Accelerometer).

Provides a "Heads-Up" compass overlay that points users physically toward their destination, bridging the digital map with the real world.

High-Performance Vector Rendering:

Built entirely with Jetpack Compose.

Uses custom Canvas Drawing logic to render the campus graph, enabling smooth zooming, panning, and dynamic path highlighting without the overhead of heavy map libraries.

# 🛠️ Tech Stack & Architecture
Language: Kotlin 
UI Toolkit: Jetpack Compose (Material 3) 
Architecture: MVVM (Model-View-ViewModel) + Clean Architecture principles 
Networking: Retrofit + OkHttp (REST API integration)  
AI/ML: Google Gemini API (Generative AI) 
Hardware: CameraX, SensorManager 
Concurrency: Kotlin Coroutines & Flow
 
