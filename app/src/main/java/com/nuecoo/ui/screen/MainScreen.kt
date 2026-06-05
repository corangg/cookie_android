package com.nuecoo.ui.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.nuecoo.ui.theme.NavBackground
import com.nuecoo.ui.theme.White

sealed class BottomNavItem(
    val route: String,
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
    val selectedColor: Color
) {
    object Oven : BottomNavItem("oven", R.string.menu_main_oven, R.drawable.ic_oven, White)
    object Collection : BottomNavItem("collection", R.string.menu_main_collection, R.drawable.ic_collection, White)

    object Menu : BottomNavItem("menu", R.string.menu_main_menu, R.drawable.ic_menu, White)
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

    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(44.dp))
            .background(NavBackground)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        bottomNavItems.forEach { item ->
            val isSelected =
                currentDestination?.hierarchy?.any { it.route == item.route } == true

            BottomNavItem(
                item = item,
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .then(
                    if (selected) {
                        Modifier
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(MainBorder)
                            .padding(horizontal = 18.dp)
                    } else {
                        Modifier.padding(horizontal = 18.dp)
                    }
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(item.icon),
                contentDescription = null,
                colorFilter = if (selected) ColorFilter.tint(item.selectedColor) else null,
                modifier = Modifier.size(if(selected)30.dp else 24.dp)
            )

            if (selected) {
                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(item.title),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
