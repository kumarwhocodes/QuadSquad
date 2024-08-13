package com.kumar.quadsquad.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kumar.quadsquad.R
import com.kumar.quadsquad.core.BottomNavBar
import com.kumar.quadsquad.logic.performPathfinding
import com.kumar.quadsquad.ui.theme.backgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var statusMessage by remember { mutableStateOf("") }
    var showImage by remember { mutableStateOf(false) }

    val imageResId = R.drawable.image3 // Use resource ID for the drawable
    val start = Pair(14, 7)
    val end = Pair(2, 1)
    val scalingFactor = 2.6F // Scaling factor for the line width

    Scaffold(modifier = Modifier, topBar = {}, bottomBar = {
        BottomNavBar(navController = navController)
    }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), color = backgroundColor
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedItem by remember { mutableStateOf("Item") }

                    val items = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V")

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedItem,
                            onValueChange = {},
                            label = { Text(text = "Choose the item") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Black,
                                unfocusedLabelColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedTextColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                focusedBorderColor = Color.Black
                            ),
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            items.forEach { option: String ->
                                DropdownMenuItem(text = { Text(text = option) }, onClick = {
                                    expanded = false
                                    selectedItem = option
                                })
                            }
                        }
                    }

                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        onClick = {
                        showImage = false
                        performPathfinding(
                            context, imageResId, start, end, scalingFactor
                        ) { bitmap, message ->
                            imageBitmap = bitmap?.asImageBitmap()
                            statusMessage = message
                            showImage = true
                        }
                    }) {
                        Text(text = "Find Path")
                    }

                }
                if (showImage && imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(text = statusMessage, modifier = Modifier.padding(top = 16.dp))
                }

            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    UserScreen(navController = rememberNavController())
}