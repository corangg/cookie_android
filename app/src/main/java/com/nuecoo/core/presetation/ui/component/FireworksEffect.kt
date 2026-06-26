package com.nuecoo.core.presetation.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onSizeChanged
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val PARTICLE_COLORS = listOf(
    Color(0xFFE0750F), Color(0xFFEEB85E), Color(0xFFCF7639),
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFF45B7D1),
    Color(0xFFFED766), Color(0xFFFF8B94), Color(0xFFA8E6CF),
    Color(0xFF9B59B6), Color(0xFF2ECC71), Color(0xFFE74C3C),
)

private const val GRAVITY = 900f

private data class Particle(
    val startX: Float,
    val startY: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val isRect: Boolean,
    val startRotation: Float,
    val rotationSpeed: Float,
    val delay: Float  // 폭발 시작 지연(초) — 여러 폭발 지점을 순차 발생시킴
)

@Composable
fun FireworksEffect(
    isVisible: Boolean,
    durationSeconds: Float = 3f,
    modifier: Modifier = Modifier
) {
    val durationMs = (durationSeconds * 1000).toInt()
    val progress = remember { Animatable(0f) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val particles = remember { mutableListOf<Particle>() }

    LaunchedEffect(isVisible, durationSeconds) {
        if (!isVisible) {
            progress.snapTo(0f)
            return@LaunchedEffect
        }
        snapshotFlow { canvasSize }.first { it != Size.Zero }
        particles.clear()
        particles.addAll(buildParticles(canvasSize))
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
    }

    Canvas(
        modifier = modifier.onSizeChanged {
            canvasSize = Size(it.width.toFloat(), it.height.toFloat())
        }
    ) {
        if (!isVisible) return@Canvas

        val elapsed = progress.value * durationSeconds

        particles.forEach { p ->
            val t = (elapsed - p.delay).coerceAtLeast(0f)
            val x = p.startX + p.vx * t
            val y = p.startY + p.vy * t + 0.5f * GRAVITY * t * t
            val alpha = (1f - t / durationSeconds).coerceIn(0f, 1f)

            if (alpha <= 0f) return@forEach

            val rotation = p.startRotation + p.rotationSpeed * t

            withTransform({
                translate(x, y)
                rotate(degrees = rotation, pivot = Offset.Zero)
            }) {
                if (p.isRect) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(-p.size, -p.size * 0.35f),
                        size = Size(p.size * 2f, p.size * 0.7f)
                    )
                } else {
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.size,
                        center = Offset.Zero
                    )
                }
            }
        }
    }
}

private fun buildParticles(canvasSize: Size): List<Particle> {
    // 화면 상단부에 5개 폭발 지점, delay로 순차 발생
    val bursts = listOf(
        Offset(canvasSize.width * 0.22f, canvasSize.height * 0.28f) to 0.00f,
        Offset(canvasSize.width * 0.78f, canvasSize.height * 0.22f) to 0.12f,
        Offset(canvasSize.width * 0.50f, canvasSize.height * 0.14f) to 0.06f,
        Offset(canvasSize.width * 0.35f, canvasSize.height * 0.42f) to 0.20f,
        Offset(canvasSize.width * 0.68f, canvasSize.height * 0.36f) to 0.16f,
    )

    return bursts.flatMap { (center, delay) ->
        List(22) { i ->
            // 360도 균등 분배 + 약간의 랜덤 퍼짐
            val base = (i.toFloat() / 22f) * 2f * PI.toFloat()
            val angle = base + (Random.nextFloat() - 0.5f) * 0.4f
            val speed = Random.nextFloat() * 520f + 260f

            Particle(
                startX = center.x,
                startY = center.y,
                vx = cos(angle) * speed,
                // 위쪽으로 살짝 편향 — 더 자연스러운 폭죽 모양
                vy = sin(angle) * speed - Random.nextFloat() * 120f,
                color = PARTICLE_COLORS[Random.nextInt(PARTICLE_COLORS.size)],
                size = Random.nextFloat() * 5f + 3f,
                isRect = Random.nextBoolean(),
                startRotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                delay = delay
            )
        }
    }
}
