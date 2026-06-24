package com.nuecoo.feature.auth.presentation.signup.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.ui.component.DefaultAuthButton
import com.nuecoo.core.ui.component.DefaultTextField
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
fun SignUpNicknameScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val step by viewModel.signUpStep.collectAsStateWithLifecycle()
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateSignUpStep(4)
    }

    SignUpNicknameScreenContent(
        isLoading = isLoading,
        step = step,
        nickname = nickname,
        onNicknameChanged = viewModel::setNickname,
        onBack = { navController.popBackStack() },
        onNext = { navController.navigate(Route.SignUp.BIRTH) },
        onCancelLoading = viewModel::cancelCurrentWork
    )
}

@Composable
private fun SignUpNicknameScreenContent(
    isLoading: Boolean,
    step: Int,
    nickname: String,
    onNicknameChanged: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onCancelLoading: () -> Unit
) {
    AuthScreenWrapper {
        val bottomPadding = imeBottomPadding

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
                    .padding(top = 28.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_nickname_main)
            )//메인 텍스트

            TextFieldNicknameItem(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 6.dp),
                value = nickname,
                onValueChanged = onNicknameChanged,
            )//닉네임 입력 텍스트 필드

            Spacer(Modifier.weight(1f))

            DefaultAuthButton(
                modifier = Modifier.padding(bottom = bottomPadding),
                title = stringResource(R.string.next),
                background = MainButton,
                titleColor = White,
                enabled = nickname.isNotEmpty(),
                onClick = onNext
            )//하단 버튼
        }
        LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)
    }
}

@Composable
private fun TextFieldNicknameItem(
    modifier: Modifier,
    value: String,
    onValueChanged: (String) -> Unit
) {
    DefaultTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChanged,
        hint = stringResource(R.string.signup_nickname_hint),
    )
}