package com.nuecoo.feature.auth.presentation.signup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nuecoo.ui.theme.WidgetActive
import com.nuecoo.ui.theme.WidgetInActive

@Composable
fun SignUpRateItem(
    step: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 10.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(7) { index ->
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        color = if (index <= step) {
                            WidgetActive
                        } else {
                            WidgetInActive
                        }
                    )
            )
        }
    }
}