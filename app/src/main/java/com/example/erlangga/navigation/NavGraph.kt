package com.example.erlangga.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.erlangga.data.TokenManager
import com.example.erlangga.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.erlangga.viewmodels.TaskViewModel
import com.example.erlangga.viewmodels.AuthViewModel
import com.example.erlangga.viewmodels.PomodoroViewModel

@Composable
fun SparkNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Initialize with saved user info if exists
    var userName by remember {
        mutableStateOf(TokenManager.getUserName() ?: "User")
    }
    var userEmail by remember {
        mutableStateOf(TokenManager.getUserEmail() ?: "user@spark.app")
    }
    var isInitialCheckDone by remember { mutableStateOf(false) }

    // Shared ViewModels
    val taskViewModel: TaskViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val pomodoroViewModel: PomodoroViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    // Check for saved auth on start
    LaunchedEffect(Unit) {
        authViewModel.checkSavedAuth()

        // If we have a valid token, load tasks immediately
        if (TokenManager.hasValidToken()) {
            taskViewModel.loadTasks()
        }
    }

    // Auto-login if token exists (only when coming from auth screen)
    LaunchedEffect(authState) {
        if (!isInitialCheckDone && authState is AuthViewModel.AuthState.Success) {
            val success = authState as AuthViewModel.AuthState.Success
            userName = success.userName
            userEmail = success.userEmail

            // Only navigate if we're not already at home
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != Screen.Home.route) {
                taskViewModel.loadTasks()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            }
            isInitialCheckDone = true
        } else if (authState is AuthViewModel.AuthState.Idle) {
            isInitialCheckDone = true
        }
    }

    // Determine start destination based on saved token
    val startDestination = if (TokenManager.hasValidToken()) Screen.Home.route else Screen.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Auth.route) {
            AuthScreenWithGoogle(
                onLoginSuccess = { name, email ->
                    userName = name
                    userEmail = email
                    // Load tasks after successful login
                    taskViewModel.loadTasks()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                userName = userName,
                onNavigateToTasks = {
                    navController.navigate(Screen.Tasks.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToPomodoro = { navController.navigate(Screen.Pomodoro.route) },
                onNavigateToFocus = { navController.navigate(Screen.Focus.route) },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.Analytics.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                taskViewModel = taskViewModel,
                pomodoroViewModel = pomodoroViewModel
            )
        }

        composable(Screen.Tasks.route) {
            TasksScreen(
                taskViewModel = taskViewModel
            )
        }

        composable(Screen.Pomodoro.route) {
            PomodoroScreen(
                onNavigateBack = { navController.navigateUp() },
                pomodoroViewModel = pomodoroViewModel
            )
        }

        composable(Screen.Focus.route) {
            FocusScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                userName = userName,
                userEmail = userEmail,
                onNavigateBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNameUpdated = { newName ->
                    userName = newName
                }
            )
        }

        composable(Screen.Notification.route) {
            NotificationScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Widget.route) {
            WidgetScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
