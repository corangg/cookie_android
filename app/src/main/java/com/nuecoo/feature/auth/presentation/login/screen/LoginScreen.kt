package com.nuecoo.feature.auth.presentation.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.ui.component.NueCooButton
import com.nuecoo.ui.component.NueCooTextField
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.NueCooTheme
import com.nuecoo.ui.theme.SubText

@Composable
fun LoginScreen(navController: NavController) {
    LoginScreenContent()
}

@Composable
private fun LoginScreenContent() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(72.dp))

        Image(
            painter = painterResource(R.drawable.img_oven_tray),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(16.dp))
        Image(
            painter = painterResource(R.drawable.ic_youcoo_kr),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(width = 100.dp, height = 36.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "매일 열리는 쿠키 한마디",
            color = SubText,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(56.dp))

        NueCooTextField(
            value = email,
            onValueChange = { email = it },
            hint = stringResource(R.string.hint_email)
        )
        Spacer(Modifier.height(12.dp))
        NueCooTextField(
            value = password,
            onValueChange = { password = it },
            hint = stringResource(R.string.hint_pw),
            isPassword = true
        )

        Spacer(Modifier.height(32.dp))

        NueCooButton(
            text = stringResource(R.string.btn_login),
            onClick = {}
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MainBorder.copy(alpha = 0.2f)
            )
            Text(
                text = "또는",
                color = SubText,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MainBorder.copy(alpha = 0.2f)
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "계정이 없으신가요?  ",
                color = SubText,
                fontSize = 14.sp
            )
            Text(
                text = stringResource(R.string.btn_signup),
                color = MainBorder,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {}
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    NueCooTheme {
        LoginScreenContent()
    }
}
