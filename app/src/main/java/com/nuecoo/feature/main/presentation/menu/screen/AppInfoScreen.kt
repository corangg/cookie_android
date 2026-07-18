package com.nuecoo.feature.main.presentation.menu.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuecoo.BuildConfig
import com.nuecoo.R
import com.nuecoo.core.presetation.ui.component.BackButton
import com.nuecoo.core.presetation.ui.component.DefaultItemBox
import com.nuecoo.core.presetation.ui.component.MainTitleItem
import com.nuecoo.core.theme.DefaultIconBackground
import com.nuecoo.core.theme.MainBackground
import com.nuecoo.core.theme.MainText
import com.nuecoo.core.theme.MenuSubBoxBackground
import com.nuecoo.core.theme.SubText
import com.nuecoo.feature.main.presentation.menu.viewmodel.MenuViewModel


@Composable
fun AppInfoScreen(
    viewModel: MenuViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onMoveRate: () -> Unit,
    onMoveCs: () -> Unit,
    onMovePrivacy: () -> Unit,
    onMoveTerms: () -> Unit,
) {
    AppInfoScreenContent(
        onBack = onBack,
        onMoveRate = onMoveRate,
        onMoveCs = onMoveCs,
        onMovePrivacy = onMovePrivacy,
        onMoveTerms = onMoveTerms,
    )
}

@Composable
private fun AppInfoScreenContent(
    onBack: () -> Unit,
    onMoveRate: () -> Unit,
    onMoveCs: () -> Unit,
    onMovePrivacy: () -> Unit,
    onMoveTerms: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        AppInfoTopItem(modifier = Modifier.padding(top = 16.dp), onBack = onBack)//상단 타이틀 컴포넌트

        AppIconItem(
            modifier = Modifier.padding(top = 24.dp)
        )

        //AppRateItem(modifier = Modifier.padding(top = 16.dp), onClick = {}/*onMoveRate*/)//앱 평가 컴포넌트, 화면은 추후 추가
        //AppCsItem(modifier = Modifier.padding(top = 16.dp), onClick = {}/*onMoveCs*/)//문의하기 컴포넌트, 화면은 추후 추가
        AppPrivacyItem(modifier = Modifier.padding(top = 16.dp), onClick = onMovePrivacy)//개인정보 컴포넌트
        AppTermsItem(modifier = Modifier.padding(top = 16.dp), onClick = onMoveTerms)//이용약관 컴포넌트

        AppTextItem(modifier = Modifier.padding(vertical = 20.dp))//바텀 앱 정보 컴포넌트
    }
}

@Composable
private fun AppInfoTopItem(modifier: Modifier, onBack: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(modifier = Modifier, onClick = onBack)//뒤로가기 버튼

        MainTitleItem(
            modifier = Modifier.padding(start = 8.dp),
            subTitle = stringResource(R.string.text_app_info_sub_title),
            mainTitle = stringResource(R.string.text_app_info_title)
        )// 메인 타이틀
    }
}

@Composable
private fun AppIconItem(modifier: Modifier) {
    DefaultItemBox(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.img_cookie_passion_1),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )//앱 아이콘

            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.app_name),
                color = MainText,
                fontSize = 22.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontWeight = FontWeight.SemiBold,
            )//앱 이름

            AppVersionCard(modifier = modifier.padding(top = 16.dp))//앱 버전
        }
    }
}

@Composable
private fun AppVersionCard(modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MenuSubBoxBackground)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${stringResource(R.string.text_app_info_version)} · ${BuildConfig.VERSION_NAME}",
            color = SubText,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun AppRateItem(modifier: Modifier, onClick: () -> Unit) {
    AppInfoActionItem(
        modifier = modifier,
        title = stringResource(R.string.text_app_info_rate_title),
        iconRes = R.drawable.ic_rate,
        iconBackgroundColor = DefaultIconBackground,
        subTitle = stringResource(R.string.text_app_info_rate_body),
        onClick = onClick
    )
}

@Composable
private fun AppCsItem(modifier: Modifier, onClick: () -> Unit) {
    AppInfoActionItem(
        modifier = modifier,
        title = stringResource(R.string.text_app_info_cs_title),
        iconRes = R.drawable.ic_mail,
        iconBackgroundColor = DefaultIconBackground,
        subTitle = stringResource(R.string.text_app_info_cs_body),
        onClick = onClick
    )
}

@Composable
private fun AppPrivacyItem(modifier: Modifier, onClick: () -> Unit) {
    AppInfoActionItem(
        modifier = modifier,
        title = stringResource(R.string.text_app_info_privacy_title),
        iconRes = R.drawable.ic_privacy,
        iconBackgroundColor = DefaultIconBackground,
        onClick = onClick
    )
}

@Composable
private fun AppTermsItem(modifier: Modifier, onClick: () -> Unit) {
    AppInfoActionItem(
        modifier = modifier,
        title = stringResource(R.string.text_app_info_terms_title),
        iconRes = R.drawable.ic_terms,
        iconBackgroundColor = DefaultIconBackground,
        onClick = onClick
    )
}

@Composable
private fun AppTextItem(modifier: Modifier) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = "${stringResource(R.string.text_app_info_bottom_text)} ${stringResource(R.string.app_name)}",
        color = SubText,
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(R.font.title_font)),
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun AppInfoActionItem(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String? = null,
    iconRes: Int,
    iconBackgroundColor: Color,
    titleColor: Color = MainText,
    onClick: () -> Unit
) {

    DefaultItemBox(
        modifier = modifier, onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    color = titleColor,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                )

                if (subTitle != null) {
                    Text(
                        modifier = Modifier.padding(top = 6.dp),
                        text = subTitle,
                        color = SubText,
                        fontFamily = FontFamily(Font(R.font.title_font)),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.ic_next),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
            )
        }
    }
}
