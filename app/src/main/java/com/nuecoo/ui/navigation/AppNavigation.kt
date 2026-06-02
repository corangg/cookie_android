package com.nuecoo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nuecoo.ui.screen.LoginScreen
import com.nuecoo.ui.screen.MainBottomNavBar
import com.nuecoo.ui.screen.MainScreen
import com.nuecoo.ui.screen.SignUpBirthScreen
import com.nuecoo.ui.screen.SignUpEmailScreen
import com.nuecoo.ui.screen.SignUpPhoneScreen
import com.nuecoo.ui.screen.SignUpPwScreen
import com.nuecoo.ui.screen.SplashScreen
import com.nuecoo.viewmodel.SignUpViewModel

object Route {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MAIN = "main"

    object SignUp {
        const val GRAPH = "signup"
        const val EMAIL = "signup/email"
        const val PW = "signup/pw"
        const val PHONE = "signup/phone"
        const val BIRTH = "signup/birth"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.SPLASH
    ) {
        composable(Route.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Route.LOGIN) {
            LoginScreen(navController = navController)
        }
        navigation(
            startDestination = Route.SignUp.EMAIL,
            route = Route.SignUp.GRAPH
        ) {
            composable(Route.SignUp.EMAIL) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry(Route.SignUp.GRAPH) }
                val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
                SignUpEmailScreen(navController = navController, viewModel = viewModel)
            }
            composable(Route.SignUp.PW) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry(Route.SignUp.GRAPH) }
                val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
                SignUpPwScreen(navController = navController, viewModel = viewModel)
            }
            composable(Route.SignUp.PHONE) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry(Route.SignUp.GRAPH) }
                val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
                SignUpPhoneScreen(navController = navController, viewModel = viewModel)
            }
            composable(Route.SignUp.BIRTH) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry(Route.SignUp.GRAPH) }
                val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
                SignUpBirthScreen(navController = navController, viewModel = viewModel)
            }
        }
        composable(Route.MAIN) {
            MainScreen(rootNavController = navController)
        }
    }
}