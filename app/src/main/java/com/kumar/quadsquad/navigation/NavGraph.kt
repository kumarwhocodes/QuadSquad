package com.kumar.quadsquad.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.kumar.quadsquad.presentation.screens.AdminScreen
import com.kumar.quadsquad.presentation.screens.UserScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val initialScreen = "user"

    val activity = (LocalContext.current as? Activity)

    NavHost(
        navController = navController,
        startDestination = initialScreen
    ){
        composable(route = "user"){
            UserScreen(navController = navController)
        }
        composable(route = "admin"){
            AdminScreen(navController = navController)
        }
    }

}