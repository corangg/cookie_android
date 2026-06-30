package com.nuecoo.feature.auth.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.nuecoo.core.navigation.Route
import com.nuecoo.feature.auth.presentation.find.screen.FindEmailCompleteScreen
import com.nuecoo.feature.auth.presentation.find.screen.FindEmailScreen
import com.nuecoo.feature.auth.presentation.find.screen.FindPwCompleteScreen
import com.nuecoo.feature.auth.presentation.find.screen.FindPwEmailScreen
import com.nuecoo.feature.auth.presentation.find.screen.FindPwPhoneScreen
import com.nuecoo.feature.auth.presentation.find.screen.FindPwResetScreen
import com.nuecoo.feature.auth.presentation.find.viewmodel.FindPwViewModel
import com.nuecoo.feature.auth.presentation.login.screen.LoginEmailScreen
import com.nuecoo.feature.auth.presentation.login.screen.LoginHomeScreen
import com.nuecoo.feature.auth.presentation.login.screen.LoginKaKaoScreen
import com.nuecoo.feature.auth.presentation.signup.screen.SignUpCompleteScreen
import com.nuecoo.feature.auth.presentation.signup.screen.SignUpEmailScreen
import com.nuecoo.feature.auth.presentation.signup.screen.SignUpInfoScreen
import com.nuecoo.feature.auth.presentation.signup.screen.SignUpNicknameScreen
import com.nuecoo.feature.auth.presentation.signup.screen.SignUpPhoneScreen
import com.nuecoo.feature.auth.presentation.signup.screen.SignUpPwScreen
import com.nuecoo.feature.auth.presentation.signup.screen.SignUpTermsScreen
import com.nuecoo.feature.auth.presentation.signup.viewmodel.SignUpViewModel
import com.nuecoo.feature.main.presentation.menu.screen.AppPrivacyScreen
import com.nuecoo.feature.main.presentation.menu.screen.AppTermsScreen
import com.nuecoo.core.theme.MainBackground

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
            LoginEmailScreen(navController = navController)
        }
        composable(Route.Login.FIND_EMAIL) {
            FindEmailScreen(navController = navController)
        }
        composable(
            route = Route.Login.FIND_EMAIL_COMPLETE,
            arguments = listOf(navArgument("maskedEmail") { type = NavType.StringType })
        ) { backStackEntry ->
            val maskedEmail = backStackEntry.arguments?.getString("maskedEmail").orEmpty()
            FindEmailCompleteScreen(navController = navController, maskedEmail = maskedEmail)
        }
        composable(Route.Login.FIND_PW_EMAIL) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.Login.GRAPH)
            }
            val viewModel: FindPwViewModel = hiltViewModel(parentEntry)
            FindPwEmailScreen(navController = navController, viewModel = viewModel)
        }
        composable(Route.Login.FIND_PW_PHONE) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.Login.GRAPH)
            }
            val viewModel: FindPwViewModel = hiltViewModel(parentEntry)
            FindPwPhoneScreen(navController = navController, viewModel = viewModel)
        }
        composable(Route.Login.FIND_PW_RESET) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.Login.GRAPH)
            }
            val viewModel: FindPwViewModel = hiltViewModel(parentEntry)
            FindPwResetScreen(navController = navController, viewModel = viewModel)
        }
        composable(Route.Login.FIND_PW_COMPLETE) {
            FindPwCompleteScreen(navController = navController)
        }
    }

    navigation(
        startDestination = Route.SignUp.TERMS,
        route = Route.SignUp.GRAPH
    ) {
        composable(Route.SignUp.TERMS) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpTermsScreen(navController = navController, viewModel = viewModel)
        }
        composable(Route.SignUp.PHONE) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpPhoneScreen(navController = navController, viewModel = viewModel)
        }
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
        composable(Route.SignUp.NICKNAME) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpNicknameScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(Route.SignUp.INFO) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpInfoScreen(navController = navController, viewModel = viewModel)
        }
        composable(Route.SignUp.COMPLETE) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Route.SignUp.GRAPH)
            }
            val viewModel: SignUpViewModel = hiltViewModel(parentEntry)
            SignUpCompleteScreen(navController = navController, viewModel = viewModel)
        }

        composable(Route.APP_PRIVACY) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MainBackground)
                    .systemBarsPadding()
            ) {
                AppPrivacyScreen(onBack = { navController.popBackStack() })
            }
        }
        composable(Route.APP_TERMS) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MainBackground)
                    .systemBarsPadding()
            ) {
                AppTermsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
