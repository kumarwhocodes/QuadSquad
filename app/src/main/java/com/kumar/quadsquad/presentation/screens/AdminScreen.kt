package com.kumar.quadsquad.presentation.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.android.play.integrity.internal.f
import com.google.android.play.integrity.internal.s
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.kumar.quadsquad.core.BottomNavBar
import com.kumar.quadsquad.logic.processImage
import com.kumar.quadsquad.ui.theme.PrimaryColor
import com.kumar.quadsquad.ui.theme.backgroundColor
import kotlinx.coroutines.tasks.await
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

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                imageUri = uri
            })

    Scaffold(modifier = Modifier, topBar = {}, bottomBar = {
        BottomNavBar(navController = navController)
    }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = backgroundColor.copy(0.5f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        launcher.launch("image/*")
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor.copy(0.5f)
                    )
                ) {
                    Text(
                        text = "Pick Image",
                        color = Color.Black
                    )
                }

                imageUri?.let { uri ->

                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor.copy(0.5f)
                        ), onClick = {
                            var f=1;
                            db.collection("maps").get().addOnSuccessListener {
                                f = it.size()+1
                            }
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
                                    db.collection("maps").add(
                                        mapOf(
                                            "matrix" to listOfMaps,
                                            "image" to downloadUri.toString(),
                                            "floorNo" to f
                                        )
                                    )
                                }
                            }
                        }) {
                        Text(
                            text = "Upload Image",
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}