package com.nuecoo.feature.auth.presentation.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.nuecoo.core.navigation.Route
import com.nuecoo.ui.screen.LoginScreen
import com.nuecoo.ui.screen.SignUpBirthScreen
import com.nuecoo.ui.screen.SignUpEmailScreen
import com.nuecoo.ui.screen.SignUpPhoneScreen
import com.nuecoo.ui.screen.SignUpPwScreen
import com.nuecoo.ui.screen.SplashScreen
import com.nuecoo.viewmodel.SignUpViewModel

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
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
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpEmailScreen(navController = navController, viewModel = viewModel)
        }
        composable(Route.SignUp.PW) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpPwScreen(navController = navController, viewModel = viewModel)
        }
        composable(Route.SignUp.PHONE) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpPhoneScreen(navController = navController, viewModel = viewModel)
        }
        composable(Route.SignUp.BIRTH) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpBirthScreen(navController = navController, viewModel = viewModel)
        }
    }
}