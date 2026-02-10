package com.retrocam.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.retrocam.app.ui.camera.CameraScreen
import com.retrocam.app.ui.settings.SettingsScreen

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object Settings : Screen("settings")
    object Presets : Screen("presets")
}

/**
 * Main navigation host for the app
 */
@Composable
fun RetroCamNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Camera.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Camera.route) {
            CameraScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAboutClick = {
                    // TODO: Add about page or dialog if needed
                }
            )
        }
        
        composable(Screen.Presets.route) {
            // TODO: Add PresetsScreen when needed
            // For now, we can navigate here but it will be empty
        }
    }
}
