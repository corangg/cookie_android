package com.nuecoo.feature.auth.presentation.find.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.core.presetation.ui.component.BackButton
import com.nuecoo.core.presetation.ui.component.DefaultAuthButton
import com.nuecoo.core.presetation.ui.component.MainTitleItem
import com.nuecoo.feature.auth.presentation.component.DefaultScreenWrapper
import com.nuecoo.ui.theme.ItemCardBackground
import com.nuecoo.ui.theme.MailBackground
import com.nuecoo.ui.theme.MailIcon
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.White

@Composable
fun FindEmailCompleteScreen(
    navController: NavController,
    maskedEmail: String,
) {
    FindEmailCompleteScreenContent(
        email = maskedEmail,
        onBack = { navController.popBackStack() },
        onLogin = { navController.popBackStack() }
    )
}

@Composable
private fun FindEmailCompleteScreenContent(
    email: String,
    onBack: () -> Unit,
    onLogin: () -> Unit
) {
    DefaultScreenWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 24.dp)
        ) {
            FindEmailTopItem(modifier = Modifier.padding(top = 16.dp), onBack = onBack)//상단 타이틀

            ImageItem(
                modifier = Modifier
                    .padding(top = 126.dp)
                    .align(Alignment.CenterHorizontally)
            )//이미지 컴포넌트

            FindEmailMainTextItem(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .padding(start = 10.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.find_email_complete_main)
            )//메인 텍스트

            FindEmailItem(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally),
                text = email
            )//찾은 이메일 컴포넌트

            Spacer(Modifier.weight(1f))

            BottomLogInButtonItem(
                modifier = Modifier.padding(bottom = 32.dp),
                onSend = onLogin,
            )//하단 버튼 컴포넌트
        }
    }
}

@Composable
private fun FindEmailTopItem(modifier: Modifier, onBack: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(modifier = Modifier, onClick = onBack)

        MainTitleItem(
            modifier = Modifier.padding(start = 8.dp),
            subTitle = stringResource(R.string.find_email_title_sub),
            mainTitle = stringResource(R.string.find_email_title_main)
        )
    }
}

@Composable
private fun ImageItem(modifier: Modifier) {
    Box(
        modifier = modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(MailBackground)
            .border(
                width = 3.dp,
                color = White,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_mail),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MailIcon),
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)

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
private fun FindEmailItem(modifier: Modifier, text: String) {
    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ItemCardBackground)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MainText,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun BottomLogInButtonItem(
    modifier: Modifier,
    onSend: () -> Unit
) {
    DefaultAuthButton(
        modifier = modifier,
        title = stringResource(R.string.find_email_complete_login_button),
        background = MainButton,
        titleColor = White,
        enabled = true,
        onClick = onSend
    )
}