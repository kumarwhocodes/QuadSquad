package com.kumar.quadsquad.logic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.kumar.quadsquad.data.items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

suspend fun performPathfinding(
    context: Context,
    imageResId: Int,
    selectedItem: String?,
//    start: Pair<Int, Int>,
//    end: Pair<Int, Int>,
    scalingFactor: Float,
    onResult: (Bitmap?, String) -> Unit
) {
    val matrix = arrayOf(
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    )

    val (path, distance) = withContext(Dispatchers.Default) {
        findShortestPath(matrix, selectedItem)
    }

    val bitmap = BitmapFactory.decodeResource(context.resources, imageResId)
        .copy(Bitmap.Config.ARGB_8888, true)
    val pathColor = Color.BLACK

    val points = mutableListOf<Offset>()
    points.add(Offset(164.1f, 54.7f))

    var x = 164.1f
    var y = 54.7f

    for (direction in path) {
        when (direction) {
            'D' -> y += 109.4f
            'U' -> y -= 109.4f
            'R' -> x += 109.4f
            'L' -> x -= 109.4f
        }
        points.add(Offset(x, y))
    }

    Log.d("Path", "Points: $points")
    Log.d("Path", "Path Color: $pathColor")

    runBlocking(Dispatchers.IO) {
        drawPath(bitmap, points, pathColor, scalingFactor)

        // Save the image to internal storage
        val file = File(context.filesDir, "output.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    val resultBitmap = BitmapFactory.decodeFile(File(context.filesDir, "output.jpg").absolutePath)
    val message = if (distance != -1) {
        "Path found! Distance: $distance"
    } else {
        "No path found."
    }

    onResult(resultBitmap, message)
}

fun drawPath(bitmap: Bitmap, points: List<Offset>, color: Int, scalingFactor: Float) {
    val canvas = Canvas(bitmap)
    val path = android.graphics.Path()
    val paint = android.graphics.Paint().apply {
        strokeWidth = 5f * scalingFactor // Adjust this base value as needed
        style = android.graphics.Paint.Style.STROKE
    }

    if (points.isNotEmpty()) {
        path.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }
    }

    canvas.drawPath(path, paint)
}

fun findShortestPath(
    matrix: Array<IntArray>,
//    start: Pair<Int, Int>,
//    end: Pair<Int, Int>,
    selectedItem: String?
): Pair<String, Int> {
    val (startRow, startCol) = Pair(0, 1)
    val (endRow, endCol) = items.getOrDefault(selectedItem, Pair(0, 1))
    val directions = arrayOf("U", "D", "L", "R")
    val rowDir = arrayOf(-1, 1, 0, 0)
    val colDir = arrayOf(0, 0, -1, 1)

    val visited = Array(matrix.size) { BooleanArray(matrix[0].size) }
    val shortestPath = StringBuilder()
    var minDist = Int.MAX_VALUE

    fun dfs(row: Int, col: Int, path: String, dist: Int) {
        if (row == endRow && col == endCol) {
            if (dist < minDist) {
                minDist = dist
                shortestPath.clear().append(path)
            }
            return
        }

        visited[row][col] = true

        for (i in directions.indices) {
            val newRow = row + rowDir[i]
            val newCol = col + colDir[i]

            if (newRow in matrix.indices && newCol in matrix[0].indices && matrix[newRow][newCol] == 1 && !visited[newRow][newCol]) {
                dfs(newRow, newCol, path + directions[i], dist + 1)
            }
        }

        visited[row][col] = false
    }

    dfs(startRow, startCol, "", 0)
    Log.d("Path", "Shortest path: $shortestPath")
    return if (minDist == Int.MAX_VALUE) "" to -1 else shortestPath.toString() to minDist
}

fun processImage(context: Context, uri: Uri): Array<IntArray> {
    val NUM_BOXES_X = 15
    val NUM_BOXES_Y = 15
    val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
        ?: throw IllegalArgumentException("Image not found")
    val width = bitmap.width
    val height = bitmap.height

    val boxWidth = width / NUM_BOXES_X
    val boxHeight = height / NUM_BOXES_Y

    val matrix = Array(NUM_BOXES_Y) { IntArray(NUM_BOXES_X) { -1 } }

    // Define your colors (in RGB)
    val whiteColor = Triple(255, 255, 255)
    val colorD0EBE8 = Triple(208, 235, 232)
    val color00393D = Triple(0, 57, 61)

    for (boxY in 0 until NUM_BOXES_Y) {
        for (boxX in 0 until NUM_BOXES_X) {
            val startX = boxX * boxWidth
            val endX = (boxX + 1) * boxWidth
            val startY = boxY * boxHeight
            val endY = (boxY + 1) * boxHeight

            var sumR = 0
            var sumG = 0
            var sumB = 0
            var count = 0

            for (y in startY until endY) {
                for (x in startX until endX) {
                    if (x >= width || y >= height) continue
                    val color = bitmap.getPixel(x, y)
                    sumR += Color.red(color)
                    sumG += Color.green(color)
                    sumB += Color.blue(color)
                    count++
                }
            }

            // Calculate average color
            if (count > 0) {
                val avgR = (sumR / count)
                val avgG = (sumG / count)
                val avgB = (sumB / count)

                // Find the closest color
                val avgColor = Triple(avgR, avgG, avgB)
                val distances = mapOf(
                    0 to calculateColorDistance(avgColor, colorD0EBE8), // D0EBE8 -> 0
                    0 to calculateColorDistance(avgColor, color00393D), // 00393D -> 0
                    1 to calculateColorDistance(avgColor, whiteColor)   // White -> 1
                )

                // Find the color with the smallest distance
                matrix[boxY][boxX] = distances.minByOrNull { it.value }?.key ?: -1
            }
        }
    }

    return matrix
}

// Helper function to calculate the Euclidean distance between two colors
fun calculateColorDistance(color1: Triple<Int, Int, Int>, color2: Triple<Int, Int, Int>): Double {
    val (r1, g1, b1) = color1
    val (r2, g2, b2) = color2
    return Math.sqrt(((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2)).toDouble())
}