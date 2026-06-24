package com.nuecoo.feature.main.presentation.main.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nuecoo.core.navigation.Route
import com.nuecoo.feature.main.navigation.MainNavHost
import com.nuecoo.ui.theme.MainBackground

private val bottomBarHiddenRoutes = setOf(
    Route.APP_INFO,
    Route.APP_RATE,
    Route.APP_CS,
    Route.APP_PRIVACY,
    Route.APP_TERMS,
)

@Composable
fun MainScreen(rootNavController: NavController) {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = currentEntry?.destination?.route !in bottomBarHiddenRoutes

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MainBackground,
            bottomBar = { if (showBottomBar) MainBottomNavBar(navController = navController) }
        ) { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                MainNavHost(navController = navController, rootNavController = rootNavController)
            }
        }
    }
}