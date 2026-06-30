package com.nuecoo.core.presetation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.core.theme.Black
import com.nuecoo.core.theme.White

@Composable
fun DefaultAuthButton(
    background: Color = Black,
    icon: Painter? = null,
    title: String,
    titleColor: Color = White,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(20.dp)
    val resolvedBackground = if (enabled) background else background.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(resolvedBackground)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(20.dp)
                )
            }

            Text(
                text = title,
                color = titleColor,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontSize = 16.sp,
            )
        }
    }
}