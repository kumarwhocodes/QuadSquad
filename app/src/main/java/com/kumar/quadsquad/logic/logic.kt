package com.kumar.quadsquad.logic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

//@Composable
//fun ImagePathfindingApp() {
//    val context = LocalContext.current
//    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
//    var statusMessage by remember { mutableStateOf("") }
//    var showImage by remember { mutableStateOf(false) }
//
//    val imageResId = R.drawable.image3 // Use resource ID for the drawable
//    val start = Pair(14, 7)
//    val end = Pair(2, 1)
//    val scalingFactor = 2.6F // Scaling factor for the line width
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if (showImage && imageBitmap != null) {
//            Image(
//                bitmap = imageBitmap!!,
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize()
//            )
//            Text(text = statusMessage, modifier = Modifier.padding(top = 16.dp))
//        }
//
//        Button(onClick = {
//            showImage = false
//            performPathfinding(context, imageResId, start, end, scalingFactor) { bitmap, message ->
//                imageBitmap = bitmap?.asImageBitmap()
//                statusMessage = message
//                showImage = true
//            }
//        }) {
//            Text(text = "Find Path")
//        }
//    }
//}

fun performPathfinding(
    context: Context,
    imageResId: Int,
    start: Pair<Int, Int>,
    end: Pair<Int, Int>,
    scalingFactor: Float,
    onResult: (Bitmap?, String) -> Unit
) {
    val matrix = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
        intArrayOf(0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
        intArrayOf(0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
        intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
        intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
        intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
        intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0)
    )

    val (path, distance) = runBlocking(Dispatchers.Default) {
        findShortestPath(matrix, start, end)
    }

    val bitmap = BitmapFactory.decodeResource(context.resources, imageResId)
        .copy(Bitmap.Config.ARGB_8888, true)
    val pathColor = Color.BLACK

    val points = mutableListOf<Offset>()
    points.add(Offset(820f, 1531f))

    var x = 820f
    var y = 1531f

    for (direction in path) {
        when (direction) {
            'D' -> y += 105
            'U' -> y -= 105
            'R' -> x += 105
            'L' -> x -= 105
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


//@Composable
//fun ImagePathfindingApp() {
//    val context = LocalContext.current
//    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
//    var statusMessage by remember { mutableStateOf("") }
//
//    val imageResId = R.drawable.image3 // Use resource ID for the drawable
//    val start = Pair(14, 7)
//    val end = Pair(2, 1)
//    val scalingFactor = 2.6F // Scaling factor for the line width
//
//    LaunchedEffect(Unit) {
//        try {
//            val matrix = arrayOf(
//                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
//                intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
//                intArrayOf(0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
//                intArrayOf(0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
//                intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
//                intArrayOf(0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
//                intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
//                intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
//                intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
//                intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
//                intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
//                intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
//                intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
//                intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
//                intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0)
//            )
//            val matrix = processImage(context, imageResId)
//
//            val (path, distance) = withContext(Dispatchers.Default) {
//                findShortestPath(matrix, start, end)
//            }
//
//            val bitmap = BitmapFactory.decodeResource(context.resources, imageResId)
//                .copy(Bitmap.Config.ARGB_8888, true)
//            val pathColor = Color.BLACK
//
//            val points = mutableListOf<Offset>()
//            points.add(Offset(820f, 1531f))
//
//            var x = 820f
//            var y = 1531f
//
//            for (direction in path) {
//                when (direction) {
//                    'D' -> y += 105
//                    'U' -> y -= 105
//                    'R' -> x += 105
//                    'L' -> x -= 105
//                }
//                points.add(Offset(x, y))
//            }
//
//            Log.d("Path", "Points: $points")
//            Log.d("Path", "Path Color: $pathColor")
//
//            withContext(Dispatchers.IO) {
//                drawPath(bitmap, points, pathColor, scalingFactor)
//
//                // Save the image to internal storage
//                val file = File(context.filesDir, "output.jpg")
//                FileOutputStream(file).use { out ->
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//                }
//            }
//
//            imageBitmap = withContext(Dispatchers.IO) {
//                BitmapFactory.decodeFile(File(context.filesDir, "output.jpg").absolutePath)
//                    .asImageBitmap()
//            }
//            if (distance != -1) {
//                Log.d("Path", "Shortest path distance: $distance Path: $path")
//            } else {
//                Log.d("Path", "No path found")
//            }
//        } catch (e: Exception) {
//            Log.e("Path", "Error: ${e.message}")
//        }
//    }
//
//    Column {
//        if (imageBitmap != null) {
//            Image(
//                bitmap = imageBitmap!!, contentDescription = null, modifier = Modifier.fillMaxSize()
//            )
//            Log.d("Path", "ImageBitmap: ${imageBitmap!!.height} ${imageBitmap!!.width}")
//        }
//        Text(text = statusMessage, modifier = Modifier.padding(top = 16.dp))
//    }
//}

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
    matrix: Array<IntArray>, start: Pair<Int, Int>, end: Pair<Int, Int>
): Pair<String, Int> {
    val (startRow, startCol) = start
    val (endRow, endCol) = end
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

fun processImage(context: Context, resId: Int): Array<IntArray> {
    val NUM_BOXES_X = 15
    val NUM_BOXES_Y = 15
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        ?: throw IllegalArgumentException("Image not found")
    val width = bitmap.width
    val height = bitmap.height

    val boxWidth = width / NUM_BOXES_X
    val boxHeight = height / NUM_BOXES_Y

    val matrix = Array(NUM_BOXES_Y) { IntArray(NUM_BOXES_X) { -1 } }

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

            val avgR = (sumR / count)
            val avgG = (sumG / count)
            val avgB = (sumB / count)

            matrix[boxY][boxX] = when {
                avgG > avgR && avgG > avgB -> 1 // Green box
                avgR > avgG && avgR > avgB -> 0 // Red box
                avgB > avgR && avgB > avgG -> 2 // Blue box
                else -> -1 // Undefined or other color
            }
        }
    }
//
//    matrix.forEach { row ->
//        Log.d("Matrix", row.joinToString(", "))
//    }

    return matrix
}