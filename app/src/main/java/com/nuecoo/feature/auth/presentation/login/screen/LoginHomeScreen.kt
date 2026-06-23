package com.nuecoo.feature.auth.presentation.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuecoo.R
import com.nuecoo.core.ui.component.DefaultAuthButton
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.main.presentation.menu.viewmodel.MenuViewModel
import com.nuecoo.ui.theme.Black
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.NueCooTheme
import com.nuecoo.ui.theme.kakao

@Composable
fun LoginHomeScreen(
    viewModel: MenuViewModel = hiltViewModel()
) {
    LoginHomeScreenContent()
}

@Composable
private fun LoginHomeScreenContent() {
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

            Spacer(Modifier.weight(1f))

            DefaultAuthButton(
                modifier = Modifier.padding(bottom = 16.dp),
                title = stringResource(R.string.login_kakao),
                background = kakao,
                icon = painterResource(R.drawable.ic_kakao),
                titleColor = Black,
                onClick = {}
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
private fun LoginHomeScreenPreview() {
    NueCooTheme {
        LoginHomeScreenContent()
    }
}
