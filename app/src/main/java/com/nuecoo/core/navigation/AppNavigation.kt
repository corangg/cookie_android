package com.nuecoo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nuecoo.feature.auth.navigation.authNavGraph
import com.nuecoo.feature.main.presentation.main.navigation.mainNavGraph
import com.nuecoo.feature.splash.presentation.screen.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.SPLASH
    ) {
        composable(Route.SPLASH) {
            SplashScreen(navController = navController)
        }
        authNavGraph(navController)
        mainNavGraph(navController)
    }
}