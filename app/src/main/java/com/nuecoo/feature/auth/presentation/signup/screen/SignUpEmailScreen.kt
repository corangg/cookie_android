package com.nuecoo.feature.auth.presentation.signup.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.presetation.ui.component.DefaultAuthButton
import com.nuecoo.core.presetation.ui.component.DefaultTextField
import com.nuecoo.core.presetation.ui.component.LoadingOverlay
import com.nuecoo.feature.auth.domain.model.EmailCheckResult
import com.nuecoo.feature.auth.presentation.component.DefaultScreenWrapper
import com.nuecoo.feature.auth.presentation.signup.component.SignUpMainTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpRateItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpSubTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpTopItem
import com.nuecoo.feature.auth.presentation.signup.viewmodel.SignUpViewModel
import com.nuecoo.core.theme.ErrorRed
import com.nuecoo.core.theme.MainButton
import com.nuecoo.core.theme.White

@Composable
fun SignUpEmailScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val step by viewModel.signUpStep.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val isEmailResult by viewModel.isEmailResult.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateSignUpStep(2)
    }

    LaunchedEffect(isEmailResult) {
        if (isEmailResult == EmailCheckResult.Available) {
            navController.navigate(Route.SignUp.PW)
        }
    }

    SignUpEmailScreenContent(
        isLoading = isLoading,
        step = step,
        email = email,
        isEmailResult = isEmailResult,
        onEmailChanged = viewModel::setEmail,
        onBack = { navController.popBackStack() },
        onNext = viewModel::checkEmail,
        onCancelLoading = viewModel::cancelCurrentWork
    )
}

@Composable
private fun SignUpEmailScreenContent(
    isLoading: Boolean,
    step: Int,
    email: String,
    isEmailResult: EmailCheckResult?,
    onEmailChanged: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onCancelLoading: () -> Unit
) {
    DefaultScreenWrapper {
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
                text = stringResource(R.string.signup_email_main)
            )//메인 텍스트
            SignUpSubTextItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_email_sub)
            )//서브 텍스트

            TextFieldEmailItem(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 6.dp),
                email = email,
                onEmailChanged = onEmailChanged,
            )//이메일 입력 텍스트 필드

            EmailResultItem(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 6.dp),
                result = isEmailResult
            )//에러 메세지 컴포넌트

            Spacer(Modifier.weight(1f))

            DefaultAuthButton(
                modifier = Modifier.padding(bottom = bottomPadding),
                title = stringResource(R.string.next),
                background = MainButton,
                titleColor = White,
                enabled = email.isNotEmpty(),
                onClick = onNext
            )//하단 버튼
        }
        LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)
    }
}

@Composable
private fun TextFieldEmailItem(
    modifier: Modifier,
    email: String,
    onEmailChanged: (String) -> Unit
) {
    DefaultTextField(
        modifier = modifier,
        value = email,
        onValueChange = onEmailChanged,
        hint = stringResource(R.string.signup_email_hint),
    )
}

@Composable
private fun EmailResultItem(modifier: Modifier = Modifier, result: EmailCheckResult?) {
    val errorMessage = when (result) {
        EmailCheckResult.Duplicated -> stringResource(R.string.signup_email_error_duplicated)
        EmailCheckResult.NotValid -> stringResource(R.string.signup_email_error_not_valid)
        EmailCheckResult.Error -> stringResource(R.string.signup_error)
        else -> return
    }

    Text(
        text = errorMessage,
        color = ErrorRed,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}