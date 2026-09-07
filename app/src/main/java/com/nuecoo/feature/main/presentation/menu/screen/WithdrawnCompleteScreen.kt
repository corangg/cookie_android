package com.nuecoo.feature.main.presentation.menu.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nuecoo.R
import com.nuecoo.core.presentation.ui.component.DefaultAuthButton
import com.nuecoo.core.theme.MainBackground
import com.nuecoo.core.theme.MainButton
import com.nuecoo.core.theme.MainTitle
import com.nuecoo.core.theme.SubTitle
import com.nuecoo.core.theme.White
import com.nuecoo.feature.main.presentation.menu.viewmodel.WithdrawnViewModel

@Composable
fun WithdrawnCompleteScreen(viewModel: WithdrawnViewModel = hiltViewModel()) {
    WithdrawnCompleteScreenContent(onOk = viewModel::moveLogin)
}

@Composable
private fun WithdrawnCompleteScreenContent(
    onOk: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        WithdrawnCompleteImageItem(modifier = Modifier)
        WithdrawnCompleteMainItem(modifier = Modifier.fillMaxWidth().padding(top = 24.dp))
        WithdrawnCompleteSubItem(modifier = Modifier.fillMaxWidth().padding(top = 12.dp))
        Spacer(Modifier.weight(1f))
        DefaultAuthButton(
            modifier = Modifier.padding(bottom = 20.dp),
            title = stringResource(R.string.text_withdrawn_complete_ok),
            background = MainButton,
            titleColor = White,
            fontSize = 20,
            onClick = onOk
        )
    }
}

@Composable
private fun WithdrawnCompleteImageItem(modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_youcoo_kr),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.size(120.dp)
    )
}

@Composable
private fun WithdrawnCompleteMainItem(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.text_withdrawn_complete_main),
        color = MainTitle,
        fontSize = 28.sp,
        lineHeight = 28.sp,
        fontFamily = FontFamily(Font(R.font.title_font)),
        fontWeight = FontWeight.Light,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun WithdrawnCompleteSubItem(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.text_withdrawn_complete_sub),
        color = SubTitle,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontFamily = FontFamily(Font(R.font.title_font)),
        fontWeight = FontWeight.ExtraLight,
        textAlign = TextAlign.Center,
    )
}