package com.kumar.quadsquad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.navigation.compose.rememberNavController
import com.kumar.quadsquad.navigation.NavGraph
import com.kumar.quadsquad.ui.theme.SparkathonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SparkathonTheme {
                NavGraph(navController = rememberNavController())
            }
        }
    }
}

