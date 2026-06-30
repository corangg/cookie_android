package com.nuecoo.feature.auth.presentation.find.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.presetation.ui.component.BackButton
import com.nuecoo.core.presetation.ui.component.DefaultAuthButton
import com.nuecoo.core.presetation.ui.component.DefaultTextField
import com.nuecoo.core.presetation.ui.component.LoadingOverlay
import com.nuecoo.core.presetation.ui.component.MainTitleItem
import com.nuecoo.feature.auth.domain.model.PwCheckResult
import com.nuecoo.feature.auth.presentation.component.DefaultScreenWrapper
import com.nuecoo.feature.auth.presentation.find.viewmodel.FindPwViewModel
import com.nuecoo.feature.auth.presentation.signup.component.SignUpMainTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpSubTextItem
import com.nuecoo.core.theme.ErrorRed
import com.nuecoo.core.theme.MainButton
import com.nuecoo.core.theme.White

@Composable
fun FindPwResetScreen(
    navController: NavHostController,
    viewModel: FindPwViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val pw by viewModel.pw.collectAsStateWithLifecycle()
    val pwCheck by viewModel.pwCheck.collectAsStateWithLifecycle()
    val isPwResult by viewModel.isPwResult.collectAsStateWithLifecycle()

    LaunchedEffect(isPwResult) {
        if (isPwResult == PwCheckResult.Success) {
            navController.navigate(Route.Login.FIND_PW_COMPLETE){
                popUpTo(Route.Login.FIND_PW_EMAIL) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    FindPwResetScreenContent(
        isLoading = isLoading,
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
private fun FindPwResetScreenContent(
    isLoading: Boolean,
    pw: String,
    pwCheck: String,
    isPwResult: PwCheckResult?,
    onPwChanged: (String) -> Unit,
    onPwCheckChanged: (String) -> Unit,
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
            FindPwTopItem(modifier = Modifier.padding(top = 16.dp), onBack = onBack)//상단 타이틀

            SignUpMainTextItem(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.find_pw_reset_main)
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
                enabled = pw.isNotEmpty() && pwCheck.isNotEmpty(),
                onClick = onNext
            )//하단 버튼
        }
        LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)
    }
}

@Composable
private fun FindPwTopItem(modifier: Modifier, onBack: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(modifier = Modifier, onClick = onBack)

        MainTitleItem(
            modifier = Modifier.padding(start = 8.dp),
            subTitle = stringResource(R.string.find_pw_title_sub),
            mainTitle = stringResource(R.string.find_pw_title_main)
        )
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
        PwCheckResult.Error -> stringResource(R.string.find_pw_error)
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