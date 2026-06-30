package com.nuecoo.core.presetation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nuecoo.R
import com.nuecoo.core.theme.BackButtonBackground

@Composable
fun BackButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BackButtonBackground)
            .clickable(onClick = onClick),
        //.padding(horizontal = 20.dp, vertical = 20.dp)
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(20.dp)
        )
    }
}