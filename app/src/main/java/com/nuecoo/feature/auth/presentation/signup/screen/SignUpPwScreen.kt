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
import com.nuecoo.core.ui.component.DefaultAuthButton
import com.nuecoo.core.ui.component.DefaultTextField
import com.nuecoo.core.ui.component.LoadingOverlay
import com.nuecoo.feature.auth.domain.model.PwCheckResult
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.auth.presentation.signup.component.SignUpMainTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpRateItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpSubTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpTopItem
import com.nuecoo.feature.auth.presentation.signup.viewmodel.SignUpViewModel
import com.nuecoo.ui.theme.ErrorRed
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.White

@Composable
fun SignUpPwScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val step by viewModel.signUpStep.collectAsStateWithLifecycle()
    val pw by viewModel.pw.collectAsStateWithLifecycle()
    val pwCheck by viewModel.pwCheck.collectAsStateWithLifecycle()
    val isPwResult by viewModel.isPwResult.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateSignUpStep(3)
    }

    LaunchedEffect(isPwResult) {
        if (isPwResult == PwCheckResult.Success) {
            navController.navigate(Route.SignUp.NICKNAME)
        }
    }

    SignUpPwScreenContent(
        isLoading = isLoading,
        step = step,
        pw = pw,
        pwCheck = pwCheck,
        isPwResult = isPwResult,
        onPwChanged = viewModel::setPw,
        onPwCheckChanged = viewModel::setPwCheck,
        onBack = { navController.popBackStack() },
        onNext = viewModel::checkPw,
        onCancelLoading = viewModel::cancelCurrentWork
    )
}

@Composable
private fun SignUpPwScreenContent(
    isLoading: Boolean,
    step: Int,
    pw: String,
    pwCheck: String,
    isPwResult: PwCheckResult?,
    onPwChanged: (String) -> Unit,
    onPwCheckChanged: (String) -> Unit,
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
                text = stringResource(R.string.signup_pw_main)
            )//메인 텍스트
            SignUpSubTextItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_pw_sub)
            )//서브 텍스트

            TextFieldPwItem(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 6.dp),
                pw = pw,
                onValueChanged = onPwChanged,
            )//비밀번호 입력 텍스트 필드

            TextFieldPwCheckItem(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 6.dp),
                pwCheck = pwCheck,
                onValueChanged = onPwCheckChanged,
            )//비밀번호 입력 텍스트 필드

            PwResultItem(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 6.dp),
                result = isPwResult
            )//에러 메세지 컴포넌트

            Spacer(Modifier.weight(1f))

            DefaultAuthButton(
                modifier = Modifier.padding(bottom = bottomPadding),
                title = stringResource(R.string.next),
                background = MainButton,
                titleColor = White,
                enabled = pw.isNotEmpty()&&pwCheck.isNotEmpty(),
                onClick = onNext
            )//하단 버튼

            LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)
        }
    }
}

@Composable
private fun TextFieldPwItem(
    modifier: Modifier,
    pw: String,
    onValueChanged: (String) -> Unit
) {
    DefaultTextField(
        modifier = modifier,
        value = pw,
        onValueChange = onValueChanged,
        hint = stringResource(R.string.signup_pw_hint),
        isPassword = true
    )
}

@Composable
private fun TextFieldPwCheckItem(
    modifier: Modifier,
    pwCheck: String,
    onValueChanged: (String) -> Unit
) {
    DefaultTextField(
        modifier = modifier,
        value = pwCheck,
        onValueChange = onValueChanged,
        hint = stringResource(R.string.signup_pw_check_hint),
        isPassword = true
    )
}

@Composable
private fun PwResultItem(modifier: Modifier = Modifier, result: PwCheckResult?) {
    val errorMessage = when (result) {
        PwCheckResult.NotValid -> stringResource(R.string.signup_pw_error_not_valid)
        PwCheckResult.NotAccordance -> stringResource(R.string.signup_pw_error_not_accordance)
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