package com.nuecoo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nuecoo.R
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.SubBackground

sealed class BottomNavItem(val route: String, val selectedIcon: Int, val unselectedIcon: Int) {
    object Oven : BottomNavItem("oven", R.drawable.ic_oven_selected, R.drawable.ic_oven_unselected)
    object Collection : BottomNavItem(
        "collection",
        R.drawable.ic_collection_selected,
        R.drawable.ic_collection_unselected
    )

    object Menu : BottomNavItem("menu", R.drawable.ic_menu_selected, R.drawable.ic_menu_unselected)
}

val bottomNavItems = listOf(BottomNavItem.Oven, BottomNavItem.Collection, BottomNavItem.Menu)

@Composable
fun MainScreen(rootNavController: NavController) {
    val navController = rememberNavController()
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            containerColor = MainBackground,
            bottomBar = {
                MainBottomNavBar(navController = navController)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                    composable(BottomNavItem.Collection.route) {
                        CollectionScreen()
                    }
                    composable(BottomNavItem.Menu.route) {
                        MenuScreen(rootNavController = rootNavController)
                    }
                }


            }
        }
    }

}

@Composable
fun MainBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = SubBackground,
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 36.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(width = 4.dp, color = MainBorder, shape = RoundedCornerShape(50.dp))
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Box(
                        modifier = if (isSelected) Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MainBorder)
                        else Modifier.size(44.dp)
                    ) {
                        Image(
                            painter = painterResource(if (isSelected) item.selectedIcon else item.unselectedIcon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(if (isSelected) 36.dp else 32.dp)
                                .align(androidx.compose.ui.Alignment.Center)
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
