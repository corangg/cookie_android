package com.nuecoo.feature.auth.presentation.login.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nuecoo.core.theme.MainBackground
import com.nuecoo.core.theme.NueCooTheme

@Composable
fun LoginKaKaoScreen() {
    LoginKaKaoScreenContent()
}

@Composable
private fun LoginKaKaoScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginKaKaoScreenPreview() {
    NueCooTheme {
        LoginKaKaoScreenContent()
    }
}
