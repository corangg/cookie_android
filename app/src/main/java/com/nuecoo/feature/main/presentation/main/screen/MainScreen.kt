package com.nuecoo.feature.main.presentation.main.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nuecoo.feature.main.presentation.main.navigation.MainNavHost
import com.nuecoo.ui.theme.MainBackground

@Composable
fun MainScreen(rootNavController: NavController) {
    val navController = rememberNavController()
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MainBackground,
            bottomBar = { MainBottomNavBar(navController = navController) }
        ) { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                MainNavHost(navController = navController, rootNavController = rootNavController)
            }
        }
    }
}