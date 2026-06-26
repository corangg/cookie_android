package com.nuecoo.feature.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nuecoo.core.navigation.Route
import com.nuecoo.feature.main.presentation.main.screen.MainScreen

fun NavGraphBuilder.mainNavGraph() {
    composable(Route.MAIN) {
        MainScreen()
    }
}