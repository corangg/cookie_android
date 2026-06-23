package com.nuecoo.core.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.OverlayBackground
import kotlinx.coroutines.delay

private const val LOADING_TIMEOUT_MS = 10_000L
private const val DOT_ANIMATION_DURATION = 500
private const val DOT_BOUNCE_HEIGHT = -10f

@Composable
fun LoadingOverlay(isLoading: Boolean, onCancel: () -> Unit) {
    if (!isLoading) return

    BackHandler(onBack = onCancel)

    LaunchedEffect(Unit) {
        delay(LOADING_TIMEOUT_MS)
        onCancel()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val offsets = List(3) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = DOT_BOUNCE_HEIGHT,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = DOT_ANIMATION_DURATION,
                    delayMillis = index * 150,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_$index"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBackground)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent(pass = PointerEventPass.Initial)
                            .changes.forEach { it.consume() }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            offsets.forEach { offsetState ->
                val offsetY by offsetState
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .offset(y = offsetY.dp)
                        .clip(CircleShape)
                        .background(MainButton)
                )
            }
        }
    }
}
