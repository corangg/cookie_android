package com.nuecoo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MainBorder,
    onPrimary = White,
    secondary = MainButton,
    onSecondary = MainBorder,
    background = MainBackground,
    onBackground = MainBorder,
    surface = SubBackground,
    onSurface = MainBorder,
)

@Composable
fun NueCooTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
