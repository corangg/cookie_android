package com.nuecoo.feature.main.presentation.menu.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuecoo.R
import com.nuecoo.core.presetation.ui.component.BackButton
import com.nuecoo.core.presetation.ui.component.DefaultAuthButton
import com.nuecoo.core.presetation.ui.component.DefaultItemBox
import com.nuecoo.core.presetation.ui.component.LoadingOverlay
import com.nuecoo.core.presetation.ui.component.MainTitleItem
import com.nuecoo.core.theme.LogoutBackground
import com.nuecoo.core.theme.MainBackground
import com.nuecoo.core.theme.MainBorder
import com.nuecoo.core.theme.MainText
import com.nuecoo.core.theme.SubText
import com.nuecoo.core.theme.White
import com.nuecoo.core.theme.WithdrawnChecked
import com.nuecoo.core.theme.WithdrawnUnChecked
import com.nuecoo.feature.main.presentation.menu.viewmodel.WithdrawnViewModel

@Composable
fun WithdrawnScreen(
    onBack: () -> Unit,
    onMoveWithdrawnComplete: () -> Unit,
    viewModel: WithdrawnViewModel = hiltViewModel()
) {
    val isCheckedOnly by viewModel.isCheckedWithdrawn.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSuccessWithdrawn by viewModel.isSuccessWithdrawn.collectAsStateWithLifecycle()

    LaunchedEffect(isSuccessWithdrawn) {
        if (isSuccessWithdrawn) {
            onMoveWithdrawnComplete()
        }
    }

    WithdrawnScreenContent(
        onBack = onBack,
        isCheckedOnly = isCheckedOnly,
        isLoading = isLoading,
        setCheckedWithdrawn = viewModel::setCheckedWithdrawn,
        onWithdraw = viewModel::onWithdrawn,
        onCancelLoading = viewModel::cancelCurrentWork,
    )
}

@Composable
private fun WithdrawnScreenContent(
    onBack: () -> Unit,
    isCheckedOnly: Boolean = false,
    isLoading: Boolean,
    setCheckedWithdrawn: (Boolean) -> Unit,
    onCancelLoading: () -> Unit,
    onWithdraw: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        WithDrawnTopItem(modifier = Modifier.padding(top = 16.dp), onBack = onBack)//상단 타이틀 컴포넌트

        MainTextItem(modifier = Modifier.padding(top = 16.dp))//메인 텍스트 아이템

        DeleteDataItem(
            modifier = Modifier.padding(top = 16.dp),
            img = painterResource(R.drawable.ic_collection),
            mainText = stringResource(R.string.text_withdrawn_delete_cookie_main),
            subText = stringResource(R.string.text_withdrawn_delete_cookie_sub)
        )//삭제 쿠키 아이템

        DeleteDataItem(
            modifier = Modifier.padding(top = 16.dp),
            img = painterResource(R.drawable.ic_attendance_active),
            mainText = stringResource(R.string.text_withdrawn_delete_attendance_main),
            subText = stringResource(R.string.text_withdrawn_delete_attendance_sub)
        )//삭제 출석 아이템

        CheckWithdrawnItem(
            modifier = Modifier.padding(top = 0.dp),
            isCheckedOnly = isCheckedOnly,
            onShowCheckedOnlyChange = setCheckedWithdrawn
        )//체크박스 아이템

        Spacer(Modifier.weight(1f))

        DefaultAuthButton(
            modifier = Modifier,
            title = stringResource(R.string.text_withdrawn_ok),
            background = if (isCheckedOnly) WithdrawnChecked else WithdrawnUnChecked,
            titleColor = White,
            fontSize = 20,
            onClick = onWithdraw
        )//시작하기 버튼 아이템

        CancelItem(
            modifier = Modifier.padding(top = 16.dp, bottom = 36.dp),
            onClick = onBack
        ) //취소 아이템
    }
    LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)
}

@Composable
private fun WithDrawnTopItem(modifier: Modifier, onBack: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(modifier = Modifier, onClick = onBack)//뒤로가기 버튼

        MainTitleItem(
            modifier = Modifier.padding(start = 8.dp),
            subTitle = stringResource(R.string.text_withdrawn_sub_title),
            mainTitle = stringResource(R.string.text_withdrawn_title)
        )// 메인 타이틀
    }
}

@Composable
private fun MainTextItem(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.text_withdrawn_main),
            color = MainText,
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.text_withdrawn_sub),
            color = SubText,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DeleteDataItem(modifier: Modifier, img: Painter, mainText: String, subText: String) {
    DefaultItemBox(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LogoutBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = img,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column() {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = mainText,
                    color = MainText,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .padding(top = 6.dp),
                    text = subText,
                    color = SubText,
                    fontFamily = FontFamily(Font(R.font.body_medium)),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CheckWithdrawnItem(
    modifier: Modifier,
    isCheckedOnly: Boolean,
    onShowCheckedOnlyChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier.offset(y = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isCheckedOnly,
            onCheckedChange = onShowCheckedOnlyChange,
            modifier = Modifier.scale(0.7f),
            colors = CheckboxDefaults.colors(
                checkedColor = MainBorder,
                uncheckedColor = MainBorder,
                checkmarkColor = White
            )
        )

        Text(
            text = stringResource(R.string.text_withdrawn_check),
            modifier = Modifier.offset(x = (-10).dp),
            color = SubText,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CancelItem(modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            text = stringResource(R.string.text_withdrawn_cancel),
            color = SubText,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
        )
    }
}