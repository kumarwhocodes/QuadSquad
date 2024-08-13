package com.kumar.quadsquad.navigation

sealed class Screen(val route: String) {
    data object AdminScreen : Screen(route = "admin")
    data object UserScreen : Screen(route = "user")
}