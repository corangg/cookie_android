package com.nuecoo.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.ui.navigation.Route
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    var showIcon by remember { mutableStateOf(false) }
    var showTitle1 by remember { mutableStateOf(false) }
    var showTitle2 by remember { mutableStateOf(false) }

    val iconAlpha by animateFloatAsState(
        targetValue = if (showIcon) 1f else 0f,
        animationSpec = tween(durationMillis = 800), label = "icon"
    )
    val title1Alpha by animateFloatAsState(
        targetValue = if (showTitle1) 1f else 0f,
        animationSpec = tween(durationMillis = 600), label = "title1"
    )
    val title2Alpha by animateFloatAsState(
        targetValue = if (showTitle2) 1f else 0f,
        animationSpec = tween(durationMillis = 600), label = "title2"
    )

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        showIcon = true
        delay(1_000)
        showTitle1 = true
        delay(600)
        showTitle2 = true
        delay(800)
        viewModel.checkAuthState()
    }

    LaunchedEffect(isLoggedIn) {
        isLoggedIn?.let { loggedIn ->
            if (loggedIn) {
                navController.navigate(Route.MAIN) {
                    popUpTo(Route.SPLASH) { inclusive = true }
                }
            } else {
                navController.navigate(Route.LOGIN) {
                    popUpTo(Route.SPLASH) { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.mipmap.ic_launcher),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .alpha(iconAlpha)
        )
        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(R.drawable.img_oven_tray),
            contentDescription = null,
            modifier = Modifier
                .size(width = 180.dp, height = 60.dp)
                .alpha(title1Alpha)
        )
        Spacer(Modifier.height(16.dp))
        Image(
            painter = painterResource(R.drawable.img_message_paper),
            contentDescription = null,
            modifier = Modifier
                .size(width = 180.dp, height = 40.dp)
                .alpha(title2Alpha)
        )
    }
}
