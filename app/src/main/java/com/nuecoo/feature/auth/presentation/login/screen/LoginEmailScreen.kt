package com.nuecoo.feature.auth.presentation.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.presetation.ui.component.DefaultAuthButton
import com.nuecoo.core.presetation.ui.component.DefaultTextField
import com.nuecoo.core.presetation.ui.component.LoadingOverlay
import com.nuecoo.feature.auth.domain.model.LoginResult
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.auth.presentation.login.viewmodel.LoginViewModel
import com.nuecoo.ui.theme.ErrorRed
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.White

@Composable
fun LoginEmailScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val loginResult by viewModel.loginResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

 /*   LaunchedEffect(loginResult) {
        if (loginResult == LoginResult.Success) {
            navController.navigate(Route.MAIN) {
                popUpTo(Route.MAIN) {
                    inclusive = true
                }
            }
        }
    }*/

    LoginEmailScreenContent(
        isLoading = isLoading,
        navController = navController,
        email = email,
        password = password,
        loginResult = loginResult,
        onEmailChanged = viewModel::setEmail,
        onPasswordChanged = viewModel::setPassword,
        onLogin = viewModel::login,
        onCancelLoading = viewModel::cancelCurrentWork
    )
}

@Composable
private fun LoginEmailScreenContent(
    isLoading: Boolean,
    navController: NavHostController,
    email: String,
    password: String,
    loginResult: LoginResult?,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLogin: () -> Unit,
    onCancelLoading: () -> Unit,
) {
    AuthScreenWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(bottom = 16.dp)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            MainLogoItem(modifier = Modifier.padding(top = 160.dp)) //로고 이미지
            MainAppNameItem(modifier = Modifier.padding(top = 20.dp))//앱 이름

            DefaultTextField(
                modifier = Modifier.padding(top = 24.dp),
                value = email,
                onValueChange = onEmailChanged,
                hint = stringResource(R.string.login_hint_email)
            )//이메일 입력 텍스트 필드

            DefaultTextField(
                modifier = Modifier.padding(top = 16.dp),
                value = password,
                onValueChange = onPasswordChanged,
                hint = stringResource(R.string.login_hint_pw),
                isPassword = true
            )// 비밀번호 입력 텍스트 필드

            DefaultAuthButton(
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
                title = stringResource(R.string.login_btn),
                background = MainButton,
                titleColor = White,
                onClick = onLogin
            )//로그인 버튼

            LoginResultItem(
                modifier = Modifier.padding(top = 10.dp),
                loginResult = loginResult
            )//로그인 결과 메세지

            FindAuthItem(
                modifier = Modifier.padding(top = 10.dp),
                onFindEmail = { navController.navigate(Route.Login.FIND_EMAIL) },
                onFindPW = { navController.navigate(Route.Login.FIND_PW) }
            )//아이디/비밀번호 찾기 텍스트
        }
        LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)
    }
}

@Composable
private fun MainLogoItem(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.img_cookie_cheering_1),
        contentDescription = null,
        modifier = modifier.size(148.dp)
    )
}

@Composable
private fun MainAppNameItem(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.app_name),
        color = MainText,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily(Font(R.font.title_font)),
        fontSize = 40.sp,
        modifier = modifier
    )
}

@Composable
private fun LoginResultItem(modifier: Modifier = Modifier, loginResult: LoginResult?) {
    val result = stringResource(
        when (loginResult) {
            LoginResult.Empty -> R.string.login_result_empty
            LoginResult.Failed -> R.string.login_result_error
            else -> return
        }
    )

    Text(
        text = result,
        color = ErrorRed,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun FindAuthItem(
    modifier: Modifier = Modifier,
    onFindEmail: () -> Unit,
    onFindPW: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = stringResource(R.string.login_find_email),
                color = SubText,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onFindEmail)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        VerticalDivider(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(16.dp),
            thickness = 2.dp,
            color = SubText
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(R.string.login_find_pw),
                color = SubText,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onFindPW)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
