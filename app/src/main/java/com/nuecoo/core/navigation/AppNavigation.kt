package com.nuecoo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nuecoo.feature.auth.presentation.navigation.authNavGraph
import com.nuecoo.feature.main.presentation.main.navigation.mainNavGraph

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.SPLASH
    ) {
        authNavGraph(navController)
        mainNavGraph(navController)
    }
}