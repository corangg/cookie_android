package com.nuecoo.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nuecoo.core.navigation.Route
import com.nuecoo.feature.main.presentation.main.screen.BottomNavItem
import com.nuecoo.feature.main.presentation.collection.screen.CollectionScreen
import com.nuecoo.feature.main.presentation.menu.screen.AppCsScreen
import com.nuecoo.feature.main.presentation.menu.screen.AppInfoScreen
import com.nuecoo.feature.main.presentation.menu.screen.AppPrivacyScreen
import com.nuecoo.feature.main.presentation.menu.screen.AppRateScreen
import com.nuecoo.feature.main.presentation.menu.screen.AppTermsScreen
import com.nuecoo.feature.main.presentation.menu.screen.MenuScreen
import com.nuecoo.feature.main.presentation.oven.screen.OvenScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    rootNavController: NavController
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Oven.route
    ) {
        composable(BottomNavItem.Oven.route) {
            OvenScreen(
                onMoveCollection = {
                    navController.navigate(BottomNavItem.Collection.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(BottomNavItem.Collection.route) { CollectionScreen() }
        composable(BottomNavItem.Menu.route) {
            MenuScreen(
                onMoveOven = {
                    navController.navigate(BottomNavItem.Oven.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onMoveAppInfo = { navController.navigate(Route.APP_INFO) },
                onLogout = {
                    rootNavController.navigate(Route.Login.GRAPH) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Route.APP_INFO) {
            AppInfoScreen(
                onBack = { navController.popBackStack() },
                onMoveRate = { navController.navigate(Route.APP_RATE) },
                onMoveCs = { navController.navigate(Route.APP_CS) },
                onMovePrivacy = { navController.navigate(Route.APP_PRIVACY) },
                onMoveTerms = { navController.navigate(Route.APP_TERMS) },
            )
        }
        composable(Route.APP_RATE) {
            AppRateScreen(
                onBack = { navController.popBackStack() },
                onMoveStore = {}
            )
        }
        composable(Route.APP_CS) {
            AppCsScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.APP_PRIVACY) {
            AppPrivacyScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.APP_TERMS) {
            AppTermsScreen(onBack = { navController.popBackStack() })
        }
    }
}