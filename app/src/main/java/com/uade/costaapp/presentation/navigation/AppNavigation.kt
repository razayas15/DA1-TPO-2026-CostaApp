package com.uade.costaapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uade.costaapp.presentation.auth.LoginScreen
import com.uade.costaapp.presentation.detail.PropertyDetailScreen
import com.uade.costaapp.presentation.home.HomeScreen
import com.uade.costaapp.presentation.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToDetail = { propertyId ->
                    navController.navigate("detail/$propertyId")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        composable(
            route = "detail/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: return@composable
            PropertyDetailScreen(
                propertyId = propertyId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
