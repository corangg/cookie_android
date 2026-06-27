package com.nuecoo.feature.auth.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nuecoo.ui.theme.MainBackground

class DefaultScreenWrapperScope(
    val imeBottomPadding: Dp,
    boxScope: BoxScope,
) : BoxScope by boxScope

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DefaultScreenWrapper(content: @Composable DefaultScreenWrapperScope.() -> Unit) {
    val imeBottomPadding = if (WindowInsets.isImeVisible) 10.dp else 32.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .navigationBarsPadding()
            .systemBarsPadding()
    ) {
        DefaultScreenWrapperScope(imeBottomPadding, this).content()
    }
}
