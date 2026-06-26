package com.nuecoo.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nuecoo.core.presetation.viewmodel.MainViewModel
import com.nuecoo.feature.auth.navigation.authNavGraph
import com.nuecoo.feature.main.navigation.mainNavGraph
import androidx.compose.runtime.getValue
import com.nuecoo.feature.splash.presentation.screen.SplashScreen

@Composable
fun AppNavigation(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    NavHost(
        navController = navController,
        startDestination = Route.SPLASH
    ) {
        composable(Route.SPLASH) {
            SplashScreen()
        }
        authNavGraph(navController)
        mainNavGraph()
    }

    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            true -> {
                navController.navigate(Route.MAIN) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            false -> {
                navController.navigate(Route.Login.GRAPH) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            null -> {}
        }
    }
}