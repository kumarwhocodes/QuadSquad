package com.kumar.quadsquad.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kumar.quadsquad.core.BottomNavBar
import com.kumar.quadsquad.ui.theme.backgroundColor

@Preview
@Composable
private fun DefaultPreview() {
    AdminScreen(navController = rememberNavController())
}

@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
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


        }
    }
}