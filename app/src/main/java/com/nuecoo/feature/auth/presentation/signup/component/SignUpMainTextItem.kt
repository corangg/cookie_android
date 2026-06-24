package com.nuecoo.feature.auth.presentation.signup.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.ui.theme.MainText

@Composable
fun SignUpMainTextItem(modifier: Modifier, text: String, fontSize: Int = 22) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily(Font(R.font.title_font)),
        color = MainText,
        fontSize = fontSize.sp
    )
}