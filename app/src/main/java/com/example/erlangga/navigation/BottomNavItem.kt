package com.example.erlangga.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        icon = Icons.Default.Home,
        label = "Home"
    )

    object Tasks : BottomNavItem(
        route = Screen.Tasks.route,
        icon = Icons.Default.CheckCircle,
        label = "Tasks"
    )

    object Analytics : BottomNavItem(
        route = Screen.Analytics.route,
        icon = Icons.Default.BarChart,
        label = "Analytics"
    )

    object Profile : BottomNavItem(
        route = Screen.Profile.route,
        icon = Icons.Default.Person,
        label = "Profile"
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Tasks,
    BottomNavItem.Analytics,
    BottomNavItem.Profile
)
