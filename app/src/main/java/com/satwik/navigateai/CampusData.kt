package com.satwik.navigateai

object CampusData { // This is your "Database" of buildings
    val nodes = listOf(
        CampusNode( // (Bottom Center)
            id="gate_main",
            name="Main Gate (Gate 1)",
            x = 500f, y = 950f,
            type = NodeType.GATE
        ),
        CampusNode( // (The center hub)
            id="unimall",
            name="Uni Mall",
            x = 500f, y = 600f,
            type = NodeType.MALL
        ),
        CampusNode( // (Left side)
            id="lib_central",
            name="Central Library",
            x = 200f, y = 500f,
            type = NodeType.LIBRARY
        ),
        CampusNode(
            id="block_34",
            name="Block 34 (CSE)",
            x = 800f, y = 400f,
            type = NodeType.CLASSROOM
        ),
        CampusNode( // (Top Left)
            id="bh_1",
            name="Boys Hostel (BH-1)",
            x = 200f, y = 100f,
            type = NodeType.HOSTEL
        ),
        CampusNode( // (Top Right)
            id="gh_1",
            name="Girls Hostel (GH-1)",
            x = 800f, y = 100f,
            type = NodeType.HOSTEL
        ),
        CampusNode(
            id="unipolis",
            name="Unipolis Auditorium",
            x = 500f, y = 300f,
            type = NodeType.AUDITORIUM
        )
    )
    // Walkable paths connecting them
    val edges = listOf(
        PathEdge("gate_main", "unimall", 400),
        PathEdge("unimall", "lib_central", 300),
        PathEdge("unimall", "block_34", 350),
        PathEdge("unimall", "unipolis", 200),
        PathEdge("lib_central", "bh_1", 500),
        PathEdge("block_34", "gh_1", 450),
        PathEdge("unipolis", "block_34", 150)
    )
}