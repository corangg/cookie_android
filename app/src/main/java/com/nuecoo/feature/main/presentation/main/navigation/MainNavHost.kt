package com.nuecoo.feature.main.presentation.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nuecoo.feature.main.presentation.main.screen.BottomNavItem
import com.nuecoo.feature.main.presentation.collection.screen.CollectionScreen
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
        composable(BottomNavItem.Menu.route) { MenuScreen(rootNavController = rootNavController) }
    }
}