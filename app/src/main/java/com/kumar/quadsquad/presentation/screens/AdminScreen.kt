package com.kumar.quadsquad.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.kumar.quadsquad.core.BottomNavBar
import com.kumar.quadsquad.logic.processImage
import com.kumar.quadsquad.ui.theme.backgroundColor
import java.util.UUID

@Preview
@Composable
private fun DefaultPreview() {
    AdminScreen(navController = rememberNavController())
}

@Composable
fun AdminScreen(
    navController: NavController
) {
    val db = Firebase.firestore
    val storage = FirebaseStorage.getInstance().reference

    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    Scaffold(
        modifier = Modifier,
        topBar = {},
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = backgroundColor
        ) {
            Column {
                Button(onClick = {
                    launcher.launch("image/*")  // This will open the image picker
                }) {
                    Text(text = "Pick Image")
                }

                imageUri?.let { uri ->
                    Button(onClick = {
                        // Generate a unique file name for the image
                        val imageName = UUID.randomUUID().toString()
                        val imageRef = storage.child("images/$imageName")

                        val matrix = processImage(context = context, uri = uri)

                        // Upload the image to Firebase Storage
                        val listOfMaps = matrix.map { row ->
                            row.mapIndexed { index, value -> index.toString() to value }.toMap()
                        }
                        val uploadTask = imageRef.putFile(uri)
                        uploadTask.addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                db.collection("maps")
                                    .add(
                                        mapOf(
                                            "matrix" to listOfMaps,
                                            "image" to downloadUri.toString()
                                        )
                                    )
                            }
                        }
                    }) {
                        Text(text = "Upload Image")
                    }
                }
            }
        }
    }
}