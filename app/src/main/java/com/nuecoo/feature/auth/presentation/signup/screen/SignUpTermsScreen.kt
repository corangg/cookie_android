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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.nuecoo.core.presetation.ui.component.DefaultAuthButton
import com.nuecoo.core.presetation.ui.component.DefaultCheckItem
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.auth.presentation.signup.component.SignUpMainTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpRateItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpSubTextItem
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
    val checkedPrivacy by viewModel.checkedPrivacy.collectAsStateWithLifecycle()
    val checkedTerms by viewModel.checkedTerms.collectAsStateWithLifecycle()
    val allTermsChecked by viewModel.isAllTermsChecked.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateSignUpStep(0)
    }

    SignUpTermsScreenContent(
        step = step,
        checkedPrivacy = checkedPrivacy,
        checkedTerms = checkedTerms,
        allTermsChecked = allTermsChecked,
        onPrivacyChange = viewModel::setCheckedPrivacy,
        onTermsChange = viewModel::setCheckedTerms,
        onAllCheckedChange = viewModel::setAllTermsChecked,
        onBack = { navController.popBackStack() },
        onPrivacyDetail = { navController.navigate(Route.APP_PRIVACY) },
        onTermsDetail = { navController.navigate(Route.APP_TERMS) },
        onNext = { navController.navigate(Route.SignUp.PHONE) }
    )
}

@Composable
private fun SignUpTermsScreenContent(
    step: Int,
    checkedPrivacy: Boolean,
    checkedTerms: Boolean,
    allTermsChecked: Boolean,
    onPrivacyChange: (Boolean) -> Unit,
    onTermsChange: (Boolean) -> Unit,
    onAllCheckedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onPrivacyDetail: () -> Unit,
    onTermsDetail: () -> Unit,
    onNext: () -> Unit,
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
                text = stringResource(R.string.signup_terms_main)
            )//메인 텍스트
            SignUpSubTextItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_terms_sub)
            )//서브 텍스트

            AllCheckItem(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 6.dp),
                checked = allTermsChecked,
                onCheckedChange = onAllCheckedChange
            )//전체 클릭 컴포넌트

            CheckItem(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 6.dp),
                checkedPrivacy = checkedPrivacy,
                onPrivacyChange = onPrivacyChange,
                checkedTerms = checkedTerms,
                onTermsChange = onTermsChange,
                onPrivacyDetail = onPrivacyDetail,
                onTermsDetail = onTermsDetail
            )//개별 클릭 컴포넌트

            Spacer(Modifier.weight(1f))

            DefaultAuthButton(
                modifier = Modifier.padding(bottom = 32.dp),
                title = stringResource(R.string.next),
                background = MainButton,
                titleColor = White,
                enabled = allTermsChecked,
                onClick = onNext
            )//다음 버튼
        }
    }
}

@Composable
private fun AllCheckItem(
    modifier: Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
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
                onCheckedChange = onCheckedChange,
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
private fun CheckItem(
    modifier: Modifier,
    checkedPrivacy: Boolean,
    onPrivacyChange: (Boolean) -> Unit,
    checkedTerms: Boolean,
    onTermsChange: (Boolean) -> Unit,
    onPrivacyDetail: () -> Unit,
    onTermsDetail: () -> Unit,
) {
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
                    checked = checkedPrivacy,
                    onCheckedChange = onPrivacyChange,
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

                Spacer(Modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .padding(top = 2.dp)
                        .clip(shape = RoundedCornerShape(50))
                        .clickable(onClick = onPrivacyDetail),
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
                    checked = checkedTerms,
                    onCheckedChange = onTermsChange,
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

                Spacer(Modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .padding(top = 2.dp)
                        .clip(shape = RoundedCornerShape(50))
                        .clickable(onClick = onTermsDetail),
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
