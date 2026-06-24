package com.nuecoo.feature.auth.presentation.signup.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.ui.component.DefaultAuthButton
import com.nuecoo.core.ui.component.FireworksEffect
import com.nuecoo.core.ui.component.LoadingOverlay
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.auth.presentation.signup.component.SignUpMainTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpRateItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpSubTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpTopItem
import com.nuecoo.feature.auth.presentation.signup.viewmodel.SignUpViewModel
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.White

@Composable
fun SignUpCompleteScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val step by viewModel.signUpStep.collectAsStateWithLifecycle()
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val isSignupResult by viewModel.isSignupResult.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.trySignUp()
        viewModel.updateSignUpStep(6)
    }

    SignUpCompleteScreenContent(
        isLoading = isLoading,
        step = step,
        nickname = nickname,
        isSignupResult = isSignupResult,
        onBack = { navController.popBackStack() },
        onHome = {
            navController.navigate(Route.Login.HOME) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        },
        onCancelLoading = viewModel::cancelCurrentWork
    )
}

@Composable
private fun SignUpCompleteScreenContent(
    isLoading: Boolean,
    step: Int,
    nickname: String,
    isSignupResult: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onCancelLoading: () -> Unit
) {
    AuthScreenWrapper {
        val bottomPadding = imeBottomPadding
        if (!isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(horizontal = 24.dp)
            ) {
                SignUpTopItem(modifier = Modifier.padding(top = 16.dp), onBack = onBack)//상단 타이틀
                SignUpRateItem(modifier = Modifier.padding(top = 20.dp), step = step)//진행 단계

                SignUpMainTextItem(
                    modifier = Modifier
                        .padding(top = 80.dp)
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(if (isSignupResult) R.string.signup_complete_success_main else R.string.signup_complete_fail_main),
                    fontSize = 26
                )//메인 텍스트
                SignUpSubTextItem(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    text = "$nickname${stringResource(if (isSignupResult) R.string.signup_complete_success_sub else R.string.signup_complete_fail_sub)}",
                    fontSize = 16
                )//서브 텍스트

                Spacer(Modifier.weight(1f))

                DefaultAuthButton(
                    modifier = Modifier.padding(bottom = bottomPadding),
                    title = stringResource(if (isSignupResult) R.string.signup_start else R.string.signup_complete_fail_button),
                    background = MainButton,
                    titleColor = White,
                    onClick = onHome
                )//하단 버튼

            }
            if (isSignupResult) {
                FireworksEffect(
                    isVisible = true,
                    durationSeconds = 15f,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)
    }
}