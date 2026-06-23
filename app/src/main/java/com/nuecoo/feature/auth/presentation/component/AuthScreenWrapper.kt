package com.nuecoo.feature.auth.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nuecoo.ui.theme.MainBackground

@Composable
fun AuthScreenWrapper(content: @Composable BoxScope.() -> Unit) {//Auth 기본 Screen 세팅
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .navigationBarsPadding()
    ) {
        content()
    }
}
