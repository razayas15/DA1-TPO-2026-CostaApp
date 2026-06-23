package com.uade.costaapp.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uade.costaapp.presentation.auth.LoginScreen
import com.uade.costaapp.presentation.detail.PropertyDetailScreen
import com.uade.costaapp.presentation.home.HomeScreen
import com.uade.costaapp.presentation.map.MapScreen
import com.uade.costaapp.presentation.splash.SplashScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, 
        startDestination = "splash",
        enterTransition = { fadeIn(animationSpec = tween(600)) },
        exitTransition = { fadeOut(animationSpec = tween(600)) },
        popEnterTransition = { fadeIn(animationSpec = tween(600)) },
        popExitTransition = { fadeOut(animationSpec = tween(600)) }
    ) {
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
                },
                onNavigateToMap = {
                    navController.navigate("map") { launchSingleTop = true }
                },
                onNavigateToFavorites = {
                    navController.navigate("favorites") { launchSingleTop = true }
                }
            )
        }

        composable(
            route = "detail/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType }),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) },
            popEnterTransition = { fadeIn(animationSpec = tween(500)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)) }
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: return@composable
            PropertyDetailScreen(
                propertyId = propertyId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMap = { lat, lng, _ ->
                    navController.navigate("map?lat=$lat&lng=$lng") { launchSingleTop = true }
                }
            )
        }

        composable(
            route = "map?lat={lat}&lng={lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; nullable = true },
                navArgument("lng") { type = NavType.StringType; nullable = true }
            ),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) },
            popEnterTransition = { fadeIn(animationSpec = tween(500)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(500)) }
        ) { backStackEntry ->
            val latStr = backStackEntry.arguments?.getString("lat")
            val lngStr = backStackEntry.arguments?.getString("lng")
            
            val targetLat = latStr?.toDoubleOrNull()
            val targetLng = lngStr?.toDoubleOrNull()
            
            MapScreen(
                targetLat = targetLat,
                targetLng = targetLng,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { propertyId ->
                    navController.navigate("detail/$propertyId")
                }
            )
        }

        composable("profile") {
            com.uade.costaapp.presentation.profile.ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFavorites = { navController.navigate("favorites") },
                onNavigateToMap = { navController.navigate("map") { launchSingleTop = true } },
                onLogoutSuccess = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("favorites") {
            com.uade.costaapp.presentation.favorites.FavoritesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { propertyId ->
                    navController.navigate("detail/$propertyId")
                }
            )
        }
    }
}
