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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.ui.component.DefaultAuthButton
import com.nuecoo.core.ui.component.DefaultCheckItem
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.auth.presentation.signup.component.SignUpRateItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpTopItem
import com.nuecoo.feature.auth.presentation.signup.viewmodel.SignUpViewModel
import com.nuecoo.ui.theme.AccentText
import com.nuecoo.ui.theme.AuthChecked
import com.nuecoo.ui.theme.AuthUnCheckBorder
import com.nuecoo.ui.theme.DividerLine
import com.nuecoo.ui.theme.ItemCardBackground
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.White

@Composable
fun SignUpTermsScreen(
    navController: NavHostController,
    viewModel: SignUpViewModel
) {
    val step by viewModel.signUpStep.collectAsStateWithLifecycle()

    SignUpTermsScreenContent(
        step = step,
        navController = navController,
        onBack = { navController.popBackStack() },
    )
}

@Composable
private fun SignUpTermsScreenContent(
    step: Int,
    navController: NavHostController,
    onBack: () -> Unit
) {
    AuthScreenWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            SignUpTopItem(modifier = Modifier.padding(top = 16.dp), onBack = onBack)//상단 타이틀 컴포넌트
            SignUpRateItem(modifier = Modifier.padding(top = 20.dp), step = step)//진행 단계 컴포넌트
            MainTextItem(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .padding(start = 10.dp)
            )//메인 텍스트 컴포넌트
            SubTextItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(start = 10.dp)
            )//서브 텍스트 컴포넌트

            AllCheckItem(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 6.dp)
            )//체크박스 컴포넌트)

            CheckItem(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 6.dp),
                onPrivacy = { navController.navigate(Route.APP_PRIVACY) },
                onTerms = { navController.navigate(Route.APP_TERMS) }
            )
            Spacer(Modifier.weight(1f))

            DefaultAuthButton(
                modifier = Modifier.padding(bottom = 32.dp),
                title = stringResource(R.string.next),
                background = MainButton,
                titleColor = White,
                onClick = {}
            )
        }
    }
}

@Composable
private fun MainTextItem(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.signup_terms_main),
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily(Font(R.font.title_font)),
        color = MainText,
        fontSize = 22.sp
    )
}

@Composable
private fun SubTextItem(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.signup_terms_sub),
        fontWeight = FontWeight.Medium,
        color = SubText,
        fontSize = 16.sp
    )
}

@Composable
private fun AllCheckItem(modifier: Modifier) {
    var checked by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ItemCardBackground)
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultCheckItem(
                checked = checked,
                onCheckedChange = { checked = it },
                boxSize = 28.dp,
                checkedBoxColor = AuthChecked,
                checkedBorderColor = AuthChecked,
                uncheckedBorderColor = AuthUnCheckBorder
            )

            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(top = 2.dp),
                text = stringResource(R.string.signup_terms_all_agree),
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.title_font)),
                color = MainText,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CheckItem(modifier: Modifier, onPrivacy: () -> Unit, onTerms: () -> Unit) {
    var checked by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ItemCardBackground)
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DefaultCheckItem(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    boxSize = 28.dp,
                    checkedBoxColor = AuthChecked,
                    checkedBorderColor = AuthChecked,
                    uncheckedBorderColor = AuthUnCheckBorder
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .padding(top = 2.dp),
                    text = stringResource(R.string.signup_terms_essential),
                    fontWeight = FontWeight.Medium,
                    color = AccentText,
                    fontSize = 14.sp
                )

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .padding(top = 2.dp),
                    text = stringResource(R.string.signup_terms_privacy),
                    fontWeight = FontWeight.Medium,
                    color = SubText,
                    fontSize = 14.sp
                )

                Spacer(modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .padding(top = 2.dp)
                        .clip(shape = RoundedCornerShape(50))
                        .clickable(onClick = onPrivacy),
                    text = stringResource(R.string.look),
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    color = MainText,
                    fontSize = 12.sp
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                thickness = 1.dp,
                color = DividerLine
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DefaultCheckItem(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    boxSize = 28.dp,
                    checkedBoxColor = AuthChecked,
                    checkedBorderColor = AuthChecked,
                    uncheckedBorderColor = AuthUnCheckBorder
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .padding(top = 2.dp),
                    text = stringResource(R.string.signup_terms_essential),
                    fontWeight = FontWeight.Medium,
                    color = AccentText,
                    fontSize = 14.sp
                )

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .padding(top = 2.dp),
                    text = stringResource(R.string.signup_terms_terms),
                    fontWeight = FontWeight.Medium,
                    color = SubText,
                    fontSize = 14.sp
                )

                Spacer(modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .padding(top = 2.dp)
                        .clip(shape = RoundedCornerShape(50))
                        .clickable(onClick = onTerms),
                    text = stringResource(R.string.look),
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    color = MainText,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun NextButton(modifier: Modifier) {
    //DefaultAuthButton()
}