package com.nuecoo.feature.auth.presentation.find.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.navigation.NavHostController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.presetation.ui.component.BackButton
import com.nuecoo.core.presetation.ui.component.DefaultAuthButton
import com.nuecoo.core.presetation.ui.component.DefaultTextField
import com.nuecoo.core.presetation.ui.component.LoadingOverlay
import com.nuecoo.core.presetation.ui.component.MainTitleItem
import com.nuecoo.feature.auth.domain.model.VerificationResult
import com.nuecoo.feature.auth.presentation.component.DefaultScreenWrapper
import com.nuecoo.feature.auth.presentation.find.viewmodel.FindPwViewModel
import com.nuecoo.core.theme.ErrorRed
import com.nuecoo.core.theme.MainButton
import com.nuecoo.core.theme.MainText
import com.nuecoo.core.theme.MenuSubBoxBackground
import com.nuecoo.core.theme.SubText
import com.nuecoo.core.theme.White

@Composable
fun FindPwPhoneScreen(
    navController: NavHostController,
    viewModel: FindPwViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val phone by viewModel.phone.collectAsStateWithLifecycle()
    val code by viewModel.code.collectAsStateWithLifecycle()
    val isCodeSent by viewModel.isCodeSent.collectAsStateWithLifecycle()
    val findResult by viewModel.isFindResult.collectAsStateWithLifecycle()

    LaunchedEffect(findResult){
        if (findResult == VerificationResult.Success) {
            navController.navigate(Route.Login.FIND_PW_RESET)
        }
    }


    FindPwPhoneScreenContent(
        isLoading = isLoading,
        phone = phone,
        code = code,
        findResult = findResult,
        isCodeSent = isCodeSent,
        onPhoneChanged = viewModel::setPhone,
        onCodeSend = viewModel::sendCode,
        onCodeChanged = viewModel::setCode,
        onBack = { navController.popBackStack() },
        onNext = viewModel::checkCode,
        onCancelLoading = viewModel::cancelCurrentWork
    )
}

@Composable
private fun FindPwPhoneScreenContent(
    isLoading: Boolean,
    phone: String,
    code: String,
    findResult: VerificationResult?,
    isCodeSent: VerificationResult?,
    onPhoneChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    onCodeSend: () -> Unit,
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

            FindEmailMainTextItem(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.find_email_main)
            )//메인 텍스트
            FindEmailSubTextItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.find_email_sub)
            )//서브 텍스트

            TextFieldEmailItem(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 6.dp),
                phone = phone,
                onPhoneChanged = onPhoneChanged,
            )//전화번호 입력 텍스트 필드

            CodeSendResultItem(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 6.dp),
                result = isCodeSent
            )//에러 메세지 컴포넌트

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
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 6.dp),
                result = findResult
            )//에러 메세지 컴포넌트

            Spacer(Modifier.weight(1f))

            BottomButtonItem(
                modifier = Modifier.padding(bottom = bottomPadding),
                isCodeSent = isCodeSent,
                isPhoneValid = phone.length == 11,
                isCodeValid = code.length == 6,
                onSend = onCodeSend,
                onNext = onNext,
            )//하단 버튼 컴포넌트
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
private fun FindEmailMainTextItem(modifier: Modifier, text: String, fontSize: Int = 22) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily(Font(R.font.title_font)),
        color = MainText,
        fontSize = fontSize.sp
    )
}

@Composable
private fun FindEmailSubTextItem(modifier: Modifier, text: String, fontSize: Int = 16) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Medium,
        color = SubText,
        fontSize = fontSize.sp
    )
}

@Composable
private fun TextFieldEmailItem(
    modifier: Modifier,
    phone: String,
    onPhoneChanged: (String) -> Unit
) {
    DefaultTextField(
        modifier = modifier,
        value = phone,
        onValueChange = onPhoneChanged,
        hint = stringResource(R.string.find_email_hint),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        maxLength = 11
    )
}

@Composable
private fun CodeItem(
    modifier: Modifier,
    isCodeSent: VerificationResult?,
    code: String,
    onCodeChanged: (String) -> Unit,
    onCodeSend: () -> Unit
) {
    if (isCodeSent == null) return
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        DefaultTextField(
            modifier = Modifier.weight(1f),
            value = code,
            maxLength = 6,
            hint = stringResource(R.string.find_email_code_hint),
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
                text = stringResource(R.string.find_email_re_send),
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
private fun CodeSendResultItem(modifier: Modifier = Modifier, result: VerificationResult?) {
    val text = when (result) {
        VerificationResult.AlreadyRegistered -> stringResource(R.string.signup_phone_code_error_already_registered)
        VerificationResult.TooManyAttempts -> stringResource(R.string.signup_phone_code_error_iooMany_attempts)
        VerificationResult.InvalidPhoneFormat -> stringResource(R.string.signup_phone_code_error_invalid_phone_format)
        VerificationResult.SmsSendFailed -> stringResource(R.string.signup_phone_code_error_sms_send_failed)
        VerificationResult.Unauthenticated -> stringResource(R.string.signup_phone_code_error_unauthenticated)
        VerificationResult.Unknown -> stringResource(R.string.signup_phone_code_error)
        else -> return
    }

    Text(
        text = text,
        color = ErrorRed,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun PhoneResultItem(modifier: Modifier = Modifier, result: VerificationResult?) {
    if (result == null || result == VerificationResult.Success) return
    val text = when (result) {
        VerificationResult.CodeMismatch -> stringResource(R.string.signup_phone_code_error_mismatch)
        VerificationResult.CodeExpired -> stringResource(R.string.signup_phone_code_error_expired)
        else -> stringResource(R.string.signup_phone_code_error)
    }

    Text(
        text = text,
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
    isCodeSent: VerificationResult?,
    isPhoneValid: Boolean,
    isCodeValid: Boolean,
    onSend: () -> Unit,
    onNext: () -> Unit
) {
    if (isCodeSent == null) {
        DefaultAuthButton(
            modifier = modifier,
            title = stringResource(R.string.find_email_send_code),
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