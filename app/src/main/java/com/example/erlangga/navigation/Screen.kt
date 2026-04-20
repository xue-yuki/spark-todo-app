package com.example.erlangga.navigation

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object Tasks : Screen("tasks")
    data object AddTask : Screen("add_task")
    data object Pomodoro : Screen("pomodoro")
    data object Focus : Screen("focus")
    data object Analytics : Screen("analytics")
    data object Profile : Screen("profile")
    data object Notification : Screen("notification")
    data object Widget : Screen("widget")
}
