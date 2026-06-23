package com.nuecoo.feature.auth.presentation.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavHostController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.ui.component.DefaultAuthButton
import com.nuecoo.core.ui.component.DefaultTextField
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.auth.presentation.login.viewmodel.LoginViewModel
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.White

@Composable
fun LoginEmailScreen(navController: NavHostController, viewModel: LoginViewModel = hiltViewModel()) {
    LoginEmailScreenContent(navController = navController)
}

@Composable
private fun LoginEmailScreenContent(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            Image(
                painter = painterResource(R.drawable.img_cookie_cheering_1),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 160.dp)
                    .size(148.dp)
            )//로고 이미지

            Text(
                text = stringResource(R.string.app_name),
                color = MainText,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontSize = 40.sp,
                modifier = Modifier.padding(top = 20.dp)
            )//앱 이름

            DefaultTextField(
                modifier = Modifier.padding(top = 24.dp),
                value = email,
                onValueChange = { email = it },
                hint = stringResource(R.string.login_hint_email)
            )//이메일 입력 텍스트 필드

            DefaultTextField(
                modifier = Modifier.padding(top = 16.dp),
                value = password,
                onValueChange = { password = it },
                hint = stringResource(R.string.login_hint_pw),
                isPassword = true
            )// 비밀번호 입력 텍스트 필드

            DefaultAuthButton(
                modifier = Modifier.padding(top = 20.dp),
                title = stringResource(R.string.login_btn),
                background = MainButton,
                titleColor = White,
                onClick = {}
            )//로그인 버튼

            FindAuthItem(
                modifier = Modifier.padding(top = 20.dp),
                onFindEmail = { navController.navigate(Route.Login.FIND_EMAIL) },
                onFindPW = { navController.navigate(Route.Login.FIND_PW) }
            )//아이디/비밀번호 찾기 텍스트
        }
    }
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
