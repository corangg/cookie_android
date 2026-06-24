package com.nuecoo.feature.auth.presentation.signup.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nuecoo.ui.theme.SubText

@Composable
fun SignUpSubTextItem(modifier: Modifier, text: String, fontSize: Int = 16) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Medium,
        color = SubText,
        fontSize = fontSize.sp
    )
}