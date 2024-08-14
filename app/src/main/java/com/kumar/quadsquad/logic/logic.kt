package com.kumar.quadsquad.logic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.kumar.quadsquad.data.itemsPosition
import com.kumar.quadsquad.data.itemsWalkPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

suspend fun performPathfinding(
    context: Context,
    imageResId: Int,
    selectedItem: String?,
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

    val w=bitmap.width
    val h=bitmap.height

    val points = mutableListOf<Offset>()
    points.add(Offset((w/15 + w/30).toFloat(), (w/30).toFloat()))

    var x = (w/15 + w/30).toFloat()
    var y = (w/30).toFloat()

    for (direction in path) {
        when (direction) {
            'D' -> y += (w/15).toFloat()
            'U' -> y -= (w/15).toFloat()
            'R' -> x += (w/15).toFloat()
            'L' -> x -= (w/15).toFloat()
        }
        points.add(Offset(x, y))
    }

    Log.d("Path", "Points: $points")
    Log.d("Path", "Path Color: $pathColor")

    runBlocking(Dispatchers.IO) {
        drawPath(bitmap, points, pathColor, scalingFactor,selectedItem)

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

fun drawPath(
    bitmap: Bitmap,
    points: List<Offset>,
    color: Int,
    scalingFactor: Float,
    selectedItem: String?
) {
    val canvas = Canvas(bitmap)
    val path = android.graphics.Path()
    val paint = android.graphics.Paint().apply {
        strokeWidth = 5f * scalingFactor // Adjust this base value as needed
        style = android.graphics.Paint.Style.STROKE
        this.color = color
    }

    if (points.isNotEmpty()) {
        path.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }
    }

    canvas.drawPath(path, paint)

    if (points.isNotEmpty()) {
        val endPoint = points.last()
        val endPointPaint = android.graphics.Paint().apply {
            style = android.graphics.Paint.Style.FILL
            this.color = Color.RED
        }
        val (endRow,endCol) = itemsPosition.getOrDefault(selectedItem, Pair(0, 1))
        // Draw a circle or marker at the end point
        Log.d("Path", "$endPoint $endRow $endCol")
        canvas.drawCircle(((bitmap.width/30)+((endCol)*(bitmap.width/15))).toFloat(), ((bitmap.width/30)+((endRow)*(bitmap.width/15))).toFloat(), 10f * scalingFactor, endPointPaint)
    }
}

fun findShortestPath(
    matrix: Array<IntArray>,
    selectedItem: String?
): Pair<String, Int> {
    val (startRow, startCol) = Pair(0, 1)
    val (endRow, endCol) = itemsWalkPosition.getOrDefault(selectedItem, Pair(0, 1))
    val directions = arrayOf("U", "D", "L", "R")
    val rowDir = arrayOf(-1, 1, 0, 0)
    val colDir = arrayOf(0, 0, -1, 1)

    val visited = Array(matrix.size) { BooleanArray(matrix[0].size) }
    val shortestPath = StringBuilder()
    var minDist = Int.MAX_VALUE

    if(matrix[endRow][endCol]==0){
        return Pair("",-1)
    }

    fun dfs(row: Int, col: Int, path: String, dist: Int) {
        if (row == endRow && col == endCol) {
            if (dist < minDist) {
                minDist = dist
                shortestPath.clear().append(path)
            }
            return
        }
        if(dist>minDist){
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
    val numBoxesX = 15
    val numBoxesY = 15
    val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
        ?: throw IllegalArgumentException("Image not found")
    val width = bitmap.width
    val height = bitmap.height

    val boxWidth = width / numBoxesX
    val boxHeight = height / numBoxesY

    Log.d("Image", "Width: $width, Height: $height")
    val matrix = Array(numBoxesY) { IntArray(numBoxesX) { -1 } }

    // Define your colors (in RGB)
    val whiteColor = Triple(255, 255, 255)
    val colorD0EBE8 = Triple(208, 235, 232)
    val color00393D = Triple(0, 57, 61)

    for (boxY in 0 until numBoxesY) {
        for (boxX in 0 until numBoxesX) {
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

                // Find the closest color using the new logic
                val avgColor = Triple(avgR, avgG, avgB)
                matrix[boxY][boxX] = findClosestColor(avgColor)
            }
        }
    }

    matrix.forEach {
        Log.d("Matrix", it.contentToString())
    }

    return matrix
}

// Function to find the closest color based on threshold
fun findClosestColor(avgColor: Triple<Int, Int, Int>): Int {
    val whiteColor = Triple(255, 255, 255)
    val colorD0EBE8 = Triple(208, 235, 232)
    val color00393D = Triple(0, 57, 61)

    // Calculate distances
    val distToWhite = calculateColorDistance(avgColor, whiteColor)
    val distToD0EBE8 = calculateColorDistance(avgColor, colorD0EBE8)
    val distTo00393D = calculateColorDistance(avgColor, color00393D)

    // Define a threshold
    val threshold = 30.0

    // If the color is very close to white, prioritize it
    if (distToWhite < threshold) return 1

    // Otherwise, choose the closest color
    return when {
        distToD0EBE8 < distTo00393D -> 0 // D0EBE8 -> 0
        else -> 0 // 00393D -> 0
    }
}

// Function to calculate color distance (Euclidean distance in RGB space)
fun calculateColorDistance(c1: Triple<Int, Int, Int>, c2: Triple<Int, Int, Int>): Double {
    val (r1, g1, b1) = c1
    val (r2, g2, b2) = c2
    return Math.sqrt(((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2)).toDouble())
}