package com.nuecoo.feature.main.presentation.main.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.nuecoo.R
import com.nuecoo.core.theme.White

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