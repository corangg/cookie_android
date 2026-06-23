package com.nuecoo.feature.auth.presentation.login.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.NueCooTheme

@Composable
fun LoginEmailScreen() {
    LoginEmailScreenContent()
}

@Composable
private fun LoginEmailScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginEmailScreenPreview() {
    NueCooTheme {
        LoginEmailScreenContent()
    }
}
