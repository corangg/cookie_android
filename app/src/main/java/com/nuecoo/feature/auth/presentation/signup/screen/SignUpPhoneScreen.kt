package com.nuecoo.feature.auth.presentation.signup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.nuecoo.ui.theme.ErrorRed
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MenuSubBoxBackground
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.White

@Composable
fun SignUpPhoneScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val step by viewModel.signUpStep.collectAsStateWithLifecycle()
    val phone by viewModel.phone.collectAsStateWithLifecycle()
    val code by viewModel.code.collectAsStateWithLifecycle()
    val isCodeSent by viewModel.isCodeSent.collectAsStateWithLifecycle()
    val isPhoneOk by viewModel.isPhoneOk.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateSignUpStep(1)
    }

    LaunchedEffect(isPhoneOk) {
        if (isPhoneOk) {
            navController.navigate(Route.SignUp.EMAIL)
        }
    }

    SignUpPhoneScreenContent(
        isLoading = isLoading,
        step = step,
        phone = phone,
        code = code,
        isCodeSent = isCodeSent,
        isPhoneOk = isPhoneOk,
        onPhoneChanged = viewModel::setPhone,
        onCodeChanged = viewModel::setCode,
        onCodeSend = viewModel::sendCode,
        onBack = { navController.popBackStack() },
        onNext = viewModel::checkCode,
        onCancelLoading = viewModel::cancelCurrentWork
    )
}


@Composable
fun SignUpPhoneScreenContent(
    isLoading: Boolean,
    step: Int,
    phone: String,
    code: String,
    isCodeSent: Boolean,
    isPhoneOk: Boolean,
    onPhoneChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    onCodeSend: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onCancelLoading: () -> Unit,
) {
    AuthScreenWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            SignUpTopItem(modifier = Modifier.padding(top = 16.dp), onBack = onBack)//상단 타이틀
            SignUpRateItem(modifier = Modifier.padding(top = 20.dp), step = step)//진행 단계

            SignUpMainTextItem(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_phone_main)
            )//메인 텍스트
            SignUpSubTextItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_phone_sub)
            )//서브 텍스트

            TextFieldPhoneItem(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 6.dp),
                phone = phone,
                onPhoneChanged = onPhoneChanged,
            )//전화번호 입력 텍스트 필드

            CodeItem(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 6.dp),
                isCodeSent = isCodeSent,
                code = code,
                onCodeChanged = onCodeChanged,
                onCodeSend = onCodeSend
            )//전화번호 인증 컴포넌트

            PhoneResultItem(
                modifier = Modifier.padding(top = 10.dp),
                result = isPhoneOk
            )//에러 메세지 컴포넌트

            Spacer(Modifier.weight(1f))

            BottomButtonItem(
                modifier = Modifier.padding(bottom = 32.dp),
                isCodeSent = isCodeSent,
                isPhoneValid = phone.length == 11,
                isCodeValid = code.length == 6,
                onSend = onCodeSend,
                onNext = onNext,
            )//하단 버튼 컴포넌트

            LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)

        }
    }
}

@Composable
private fun TextFieldPhoneItem(
    modifier: Modifier,
    phone: String,
    onPhoneChanged: (String) -> Unit
) {
    DefaultTextField(
        modifier = modifier,
        value = phone,
        onValueChange = onPhoneChanged,
        hint = stringResource(R.string.signup_phone_hint),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        maxLength = 11
    )
}

@Composable
private fun CodeItem(
    modifier: Modifier,
    isCodeSent: Boolean,
    code: String,
    onCodeChanged: (String) -> Unit,
    onCodeSend: () -> Unit
) {
    if (!isCodeSent) return
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        DefaultTextField(
            modifier = Modifier.weight(1f),
            value = code,
            maxLength = 6,
            hint = stringResource(R.string.signup_phone_code_hint),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = onCodeChanged
        )

        Box(
            modifier = Modifier
                .size(width = 96.dp, height = 56.dp)
                .padding(start = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MenuSubBoxBackground)
                .align(Alignment.CenterVertically)
                .clickable(onClick = onCodeSend),
        ) {
            Text(
                text = stringResource(R.string.signup_phone_re_send),
                color = SubText,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center),
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun PhoneResultItem(modifier: Modifier = Modifier, result: Boolean) {
    if (result) return
    Text(
        text = stringResource(R.string.signup_phone_code_error),
        color = ErrorRed,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun BottomButtonItem(
    modifier: Modifier,
    isCodeSent: Boolean,
    isPhoneValid: Boolean,
    isCodeValid: Boolean,
    onSend: () -> Unit,
    onNext: () -> Unit
) {
    if (!isCodeSent) {
        DefaultAuthButton(
            modifier = modifier,
            title = stringResource(R.string.signup_phone_send_code),
            background = MainButton,
            titleColor = White,
            enabled = isPhoneValid,
            onClick = onSend
        )
    } else {
        DefaultAuthButton(
            modifier = modifier,
            title = stringResource(R.string.next),
            background = MainButton,
            titleColor = White,
            enabled = isCodeValid,
            onClick = onNext
        )
    }
}