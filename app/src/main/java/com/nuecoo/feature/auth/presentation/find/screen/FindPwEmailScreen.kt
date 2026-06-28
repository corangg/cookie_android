package com.nuecoo.feature.auth.presentation.find.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import com.nuecoo.feature.auth.domain.model.EmailCheckResult
import com.nuecoo.feature.auth.domain.model.FindEmailResult
import com.nuecoo.feature.auth.domain.model.VerificationResult
import com.nuecoo.feature.auth.presentation.component.DefaultScreenWrapper
import com.nuecoo.feature.auth.presentation.find.screen.TextFieldEmailItem
import com.nuecoo.feature.auth.presentation.find.viewmodel.FindPwViewModel
import com.nuecoo.ui.theme.ErrorRed
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.White

@Composable
fun FindPwEmailScreen(
    navController: NavHostController,
    viewModel: FindPwViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val emailResult by viewModel.isEmailResult.collectAsStateWithLifecycle()

    LaunchedEffect(emailResult) {
        if (emailResult == EmailCheckResult.Duplicated) {
            navController.navigate(Route.Login.FIND_PW_PHONE)
        }
    }

    FindPwEmailScreenContent(
        isLoading = isLoading,
        email = email,
        findResult = emailResult,
        onEmailChanged = viewModel::setEmail,
        onBack = { navController.popBackStack() },
        onNext = viewModel::checkEmail,
        onCancelLoading = viewModel::cancelCurrentWork
    )
}

@Composable
private fun FindPwEmailScreenContent(
    isLoading: Boolean,
    email: String,
    findResult: EmailCheckResult?,
    onEmailChanged: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onCancelLoading: () -> Unit
){
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
                text = stringResource(R.string.find_pw_email_main)
            )//메인 텍스트

            TextFieldEmailItem(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 6.dp),
                value = email,
                onValueChanged = onEmailChanged,
            )//전화번호 입력 텍스트 필드

            EmailResultItem(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 6.dp),
                result = findResult
            )//에러 메세지 컴포넌트

            Spacer(Modifier.weight(1f))

            DefaultAuthButton(
                modifier = Modifier.padding(bottom = bottomPadding),
                title = stringResource(R.string.next),
                background = MainButton,
                titleColor = White,
                enabled = email.isNotEmpty(),
                onClick = onNext
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
private fun EmailResultItem(modifier: Modifier = Modifier, result: EmailCheckResult?) {
    val errorMessage = when (result) {
        EmailCheckResult.Available -> stringResource(R.string.find_pw_email_error_not_find)
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

@Composable
private fun TextFieldEmailItem(
    modifier: Modifier,
    value: String,
    onValueChanged: (String) -> Unit
) {
    DefaultTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChanged,
        hint = stringResource(R.string.find_pw_email_hint)
    )
}