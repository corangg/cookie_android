package com.nuecoo.feature.main.presentation.oven.screen

import android.R.id.message
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.nuecoo.R
import com.nuecoo.core.presetation.ui.component.CommonRoundButton
import com.nuecoo.core.theme.MainText
import com.nuecoo.core.theme.NueCooTheme
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import getCookieAnimationFrames
import getCookieTypeColor
import getCookieTypeMainTextRes
import getCookieTypeSubTextRes
import getOpenedCookieImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.hypot

@Composable
fun CookieOpenScreen(
    cookieData: CookieUIItemData,
    onClose: () -> Unit,
    onCookieOpened: (Int) -> Unit,
    onMoveCollection: () -> Unit
) {
    val isAlreadyOpened = cookieData.isOpened == true
    var isOpened by remember { mutableStateOf(isAlreadyOpened) }
    var isAnimating by remember { mutableStateOf(false) }
    var currentFrame by remember { mutableIntStateOf(0) }
    val animFrames = remember(cookieData.type) { getCookieAnimationFrames(cookieData.type) }
    var triggerAnimation by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    LaunchedEffect(cookieData.type) {
        withContext(Dispatchers.IO) {
            (animFrames + getOpenedCookieImage(cookieData.type)).forEach { resId ->
                runCatching { ResourcesCompat.getDrawable(context.resources, resId, null) }
            }
        }
    }

    LaunchedEffect(triggerAnimation) {
        if (!triggerAnimation || isOpened || isAnimating) return@LaunchedEffect
        isAnimating = true
        onCookieOpened(cookieData.type)

        if (animFrames.isNotEmpty()) {
            animFrames.indices.forEach { i ->
                currentFrame = i
                delay(400L)
            }
        }

        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        isOpened = true
        isAnimating = false
    }

    val displayImage = when {
        isOpened -> getOpenedCookieImage(cookieData.type)
        isAnimating -> animFrames.getOrElse(currentFrame) { cookieData.imgRes }
        else -> cookieData.imgRes
    }

    val message = cookieData.message?: stringResource(R.string.text_message_open_cookie_error)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .cookiePinchOpenDetector(
                    enabled = !isOpened && !isAnimating,
                    onOpen = {
                        triggerAnimation = true
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-36).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SetTypeMessage(cookieData.type)

                Spacer(Modifier.height(12.dp))

                val infiniteTransition = rememberInfiniteTransition(label = "")

                val rotation by infiniteTransition.animateFloat(
                    initialValue = -4f,
                    targetValue = 4f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 1200,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = ""
                )

                Image(
                    painter = painterResource(displayImage),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp)
                        .padding(top = 24.dp)
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                )

                if (!isOpened && !isAnimating) {
                    Spacer(Modifier.height(96.dp))
                    Text(
                        stringResource(R.string.text_open_cookie_sub),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
                        fontWeight = FontWeight.Thin,
                    )
                }

                AnimatedVisibility(
                    visible = isOpened,
                    enter = fadeIn(animationSpec = tween(300))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        MessageBackgroundBox(message = message)
                        Spacer(Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(horizontal = 28.dp)
                        ) {
                            CommonRoundButton(
                                text = stringResource(R.string.close),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),

                                backgroundColor = Color(0x66FFFFFF),
                                borderColor = Color.White.copy(alpha = 0.5f),
                                cornerRadius = 24.dp,
                                textColor = Color.White,

                                onClick = { onClose() }
                            )

                            CommonRoundButton(
                                text = stringResource(R.string.text_open_cookie_start_collection),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),

                                backgroundColor = getCookieTypeColor(cookieData.type),
                                cornerRadius = 24.dp,
                                textColor = Color.White,

                                onClick = { onMoveCollection() }
                            )
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp)
                .padding(end = 12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_close_white),
                contentDescription = "close",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun SetTypeMessage(type: Int) {
    val typeColor = getCookieTypeColor(type)
    val main = stringResource(getCookieTypeMainTextRes(type))
    val sub = stringResource(getCookieTypeSubTextRes(type))
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = typeColor,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = main,
            color = typeColor,
            fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = sub,
            color = MainText,
            fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
            fontWeight = FontWeight.Thin,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun MessageBackgroundBox(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.img_message_background_top),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.img_message_background_mid),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )

                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                        .padding(top = 8.dp, bottom = 8.dp),
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                    color = MainText,
                    fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
            }

            Image(
                painter = painterResource(R.drawable.img_message_background_bottom),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun Modifier.cookiePinchOpenDetector(
    enabled: Boolean,
    onOpen: () -> Unit
): Modifier {
    return if (!enabled) this
    else this.pointerInput(Unit) {
        val activePointers = mutableMapOf<Long, Offset>()
        var initialDistance = 0f

        val threshold = with(density) { 80.dp.toPx() }

        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()

                when (event.type) {
                    PointerEventType.Press -> {
                        event.changes.forEach { change ->
                            activePointers[change.id.value] = change.position
                        }

                        if (activePointers.size == 2) {
                            val pts = activePointers.values.toList()
                            initialDistance = hypot(
                                (pts[0].x - pts[1].x).toDouble(),
                                (pts[0].y - pts[1].y).toDouble()
                            ).toFloat()
                        }
                    }

                    PointerEventType.Move -> {
                        if (activePointers.size == 2) {
                            event.changes.forEach { change ->
                                activePointers[change.id.value] = change.position
                            }

                            val pts = activePointers.values.toList()
                            val currentDistance = hypot(
                                (pts[0].x - pts[1].x).toDouble(),
                                (pts[0].y - pts[1].y).toDouble()
                            ).toFloat()



                            if (currentDistance - initialDistance > threshold) {
                                onOpen()
                                activePointers.clear()
                            }
                        }
                    }

                    PointerEventType.Release -> {
                        event.changes.forEach { change ->
                            activePointers.remove(change.id.value)
                        }

                        if (activePointers.size < 2) {
                            initialDistance = 0f
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}