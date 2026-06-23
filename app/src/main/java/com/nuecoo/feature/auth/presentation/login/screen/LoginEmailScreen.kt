package com.nuecoo.feature.auth.presentation.login.screen

import android.R.attr.password
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuecoo.R
import com.nuecoo.core.ui.component.DefaultTextField
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.auth.presentation.login.viewmodel.LoginViewModel
import com.nuecoo.feature.main.presentation.menu.viewmodel.MenuViewModel
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.NueCooTheme

@Composable
fun LoginEmailScreen(viewModel: LoginViewModel = hiltViewModel()) {
    LoginEmailScreenContent()
}

@Composable
private fun LoginEmailScreenContent() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthScreenWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            )

            Text(
                text = stringResource(R.string.app_name),
                color = MainText,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontSize = 40.sp,
                modifier = Modifier.padding(top = 20.dp)
            )

            DefaultTextField(
                modifier = Modifier.padding(top = 20.dp),
                value = email,
                onValueChange = { email = it },
                hint = stringResource(R.string.login_hint_email)
            )

            DefaultTextField(
                modifier = Modifier.padding(top = 16.dp),
                value = password,
                onValueChange = { password = it },
                hint = stringResource(R.string.login_hint_pw),
                isPassword = true
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun LoginEmailScreenPreview() {
    NueCooTheme {
        LoginEmailScreenContent()
    }
}
