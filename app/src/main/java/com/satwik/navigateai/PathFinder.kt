package com.satwik.navigateai

import java.util.PriorityQueue

object PathFinder {
    // The Core Interview Function: Dijkstra's Algorithm
    fun findShortestPath(
        startId: String,
        endId: String,
        nodes: List<CampusNode>,
        edges: List<PathEdge>
    ): List<String> { // Returns a list of Node IDs (e.g. ["gate", "mall", "lib"])
        // 1. Setup: Distances map (initialized to Infinity)
        val distances = HashMap<String, Int>()
        val previous = HashMap<String, String>() // To reconstruct the path later
        // Priority Queue: Always processes the "closest" node next (The "Greedy" part)
        val queue = PriorityQueue<Pair<String, Int>> { a, b -> a.second - b.second }
        // 2. Initialize
        nodes.forEach { distances[it.id] = Int.MAX_VALUE }
        distances[startId] = 0
        queue.add(Pair(startId, 0))
        // 3. The Loop
        while (queue.isNotEmpty()) {
            val (currentId, currentDist) = queue.poll()
            // Optimization: If we found the destination, stop!
            if (currentId == endId) break
            if (currentDist > (distances[currentId] ?: Int.MAX_VALUE)) continue
            // Find neighbors
            val neighbors = edges.filter { it.fromId == currentId || it.toId == currentId }
            for (edge in neighbors) {
                // Who is the neighbor? (Edges are bidirectional)
                val neighborId = if (edge.fromId == currentId) edge.toId else edge.fromId
                val newDist = currentDist + edge.distance // Calculate new distance
                // If this new path is shorter than the old known path, update it!
                if (newDist < (distances[neighborId] ?: Int.MAX_VALUE)) {
                    distances[neighborId] = newDist
                    previous[neighborId] = currentId
                    queue.add(Pair(neighborId, newDist))
                }
            }
        }
        // 4. Reconstruct the Path (Backtracking from End -> Start)
        val path = mutableListOf<String>()
        var curr: String? = endId
        // Safety check: Did we actually reach the end?
        if (distances[endId] == Int.MAX_VALUE) return emptyList()
        while (curr != null) {
            path.add(0, curr)
            curr = previous[curr]
        }
        return path
    }
}