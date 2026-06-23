package com.nuecoo.feature.auth.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.nuecoo.core.navigation.Route
import com.nuecoo.feature.auth.presentation.login.screen.LoginEmailScreen
import com.nuecoo.feature.auth.presentation.login.screen.LoginHomeScreen
import com.nuecoo.feature.auth.presentation.login.screen.LoginKaKaoScreen
import com.nuecoo.ui.screen.SignUpBirthScreen
import com.nuecoo.ui.screen.SignUpEmailScreen
import com.nuecoo.ui.screen.SignUpPhoneScreen
import com.nuecoo.ui.screen.SignUpPwScreen
import com.nuecoo.viewmodel.SignUpViewModel

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {

    navigation(
        startDestination = Route.Login.HOME,
        route = Route.Login.GRAPH
    ) {
        composable(Route.Login.HOME) {
            LoginHomeScreen(navController = navController)
        }
        composable(Route.Login.KAKAO) {
            LoginKaKaoScreen()
        }
        composable(Route.Login.EMAIL) {
            LoginEmailScreen()
        }
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
