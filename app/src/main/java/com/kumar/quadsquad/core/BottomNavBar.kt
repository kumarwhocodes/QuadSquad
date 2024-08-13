package com.kumar.quadsquad.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kumar.quadsquad.navigation.Screen
import com.kumar.quadsquad.ui.theme.PrimaryColor
import com.kumar.quadsquad.ui.theme.backgroundColor

@Preview
@Composable
private fun DefaultPreview() {
    BottomNavBar(navController = rememberNavController())
}

@Composable
fun BottomNavBar(
    navController: NavController,
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier, containerColor = backgroundColor.copy(0.5f)
    ) {

        NavigationBarItem(selected = currentRoute == Screen.UserScreen.route, onClick = {
            if (currentRoute != Screen.UserScreen.route) {
                navController.navigate(Screen.UserScreen.route)
            }
        }, icon = {
            Icon(
                imageVector = if (currentRoute == Screen.UserScreen.route) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = "User",
                tint = if (currentRoute == Screen.UserScreen.route) PrimaryColor else Color.Gray
            )
        }, label = {
            Text(
                text = if (currentRoute == Screen.UserScreen.route) "User" else "",
                color = if (currentRoute == Screen.UserScreen.route) PrimaryColor else Color.Gray
            )
        }, colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = Color.Gray,
            selectedIconColor = PrimaryColor,
            selectedTextColor = PrimaryColor,
            unselectedTextColor = Color.Transparent,
            indicatorColor = PrimaryColor.copy(0.25f)
        )
        )

        NavigationBarItem(selected = currentRoute == Screen.AdminScreen.route, onClick = {
            if (currentRoute != Screen.AdminScreen.route) {
                navController.navigate(Screen.AdminScreen.route)
            }
        }, icon = {
            Icon(
                imageVector = if (currentRoute == Screen.AdminScreen.route) Icons.Filled.Build else Icons.Outlined.Build,
                contentDescription = "Admin",
                tint = if (currentRoute == Screen.AdminScreen.route) PrimaryColor else Color.Gray
            )
        }, label = {
            Text(
                text = if (currentRoute == Screen.AdminScreen.route) "Admin" else "",
                color = if (currentRoute == Screen.AdminScreen.route) PrimaryColor else Color.Gray
            )
        }, colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = Color.Gray,
            selectedIconColor = PrimaryColor,
            selectedTextColor = PrimaryColor,
            unselectedTextColor = Color.Transparent,
            indicatorColor = PrimaryColor.copy(0.25f)
        )
        )


    }
}