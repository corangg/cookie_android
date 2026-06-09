package com.nuecoo.feature.main.presentation.main.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.ui.theme.MainTitle
import com.nuecoo.ui.theme.SubTitle

@Composable
fun MainTitleItem(modifier: Modifier = Modifier, subTitle: String, mainTitle: String) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = subTitle,
            color = SubTitle,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 12.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = mainTitle,
            color = MainTitle,
            fontSize = 28.sp,
            lineHeight = 28.sp,
            fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
            fontWeight = FontWeight.Light,
        )
    }
}