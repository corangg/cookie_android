package com.nuecoo.core.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nuecoo.R
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.Transparent
import com.nuecoo.ui.theme.White

@Composable
fun DefaultCheckItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    boxSize: Dp = 20.dp,
    cornerRadius: Dp = 8.dp,
    checkedBoxColor: Color = MainBorder,
    uncheckedBoxColor: Color = Transparent,
    checkedBorderColor: Color = MainBorder,
    uncheckedBorderColor: Color = MainBorder,
    checkmarkColor: Color = White,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val boxColor = if (checked) checkedBoxColor else uncheckedBoxColor
    val borderColor = if (checked) checkedBorderColor else uncheckedBorderColor

    Box(
        modifier = modifier
            .size(boxSize)
            .clip(shape)
            .drawBehind { drawRect(boxColor) }
            .border(width = 1.5.dp, color = borderColor, shape = shape)
            .clickable { onCheckedChange(!checked) },

        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = checkmarkColor,
                modifier = Modifier.size(boxSize * 0.65f)
            )
        }
    }
}
