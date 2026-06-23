package com.nuecoo.feature.auth.presentation.login.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper

@Composable
fun FindEmailScreen(navController: NavHostController) {
    AuthScreenWrapper {
        Box(modifier = Modifier.fillMaxSize())
    }
}
