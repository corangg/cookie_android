package com.nuecoo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.domain.model.CookieType
import com.nuecoo.domain.model.CookieUIItemData
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.OverlayBackground
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.hypot

fun getAnimationFrames(type: Int): List<Int> = when (type) {
    CookieType.Cheering.type -> listOf(
        R.drawable.img_cookie_cheering_2, R.drawable.img_cookie_cheering_3,
        R.drawable.img_cookie_cheering_4, R.drawable.img_cookie_cheering_5
    )
    CookieType.Consolation.type -> listOf(
        R.drawable.img_cookie_comfort_2, R.drawable.img_cookie_comfort_3,
        R.drawable.img_cookie_comfort_4, R.drawable.img_cookie_comfort_5
    )
    CookieType.Passion.type -> listOf(
        R.drawable.img_cookie_passion_2, R.drawable.img_cookie_passion_3,
        R.drawable.img_cookie_passion_4, R.drawable.img_cookie_passion_5
    )
    CookieType.Determination.type -> listOf(
        R.drawable.img_cookie_sermon_2, R.drawable.img_cookie_sermon_3,
        R.drawable.img_cookie_sermon_4, R.drawable.img_cookie_sermon_5
    )
    else -> listOf(R.drawable.img_cookie_deactive)
}

fun getOpenedImage(type: Int): Int = when (type) {
    CookieType.Cheering.type -> R.drawable.img_cookie_cheering_6
    CookieType.Consolation.type -> R.drawable.img_cookie_comfort_6
    CookieType.Passion.type -> R.drawable.img_cookie_passion_6
    CookieType.Determination.type -> R.drawable.img_cookie_sermon_6
    else -> R.drawable.img_cookie_deactive
}

@Composable
fun CookieOpenScreen(
    cookieData: CookieUIItemData,
    cookieMessages: Map<Int, List<String>>,
    onClose: () -> Unit,
    onCookieOpened: (Int) -> Unit
) {
    val isAlreadyOpened = cookieData.isOpened == true
    var isOpened by remember { mutableStateOf(isAlreadyOpened) }
    var isAnimating by remember { mutableStateOf(false) }
    var currentFrame by remember { mutableIntStateOf(0) }
    val animFrames = remember(cookieData.type) { getAnimationFrames(cookieData.type) }

    // Pinch state
    val activePointers = remember { mutableMapOf<Long, Offset>() }
    var initialDistance by remember { mutableStateOf(0f) }

    // Trigger animation when pinch opens cookie
    var triggerAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(triggerAnimation) {
        if (!triggerAnimation || isOpened || isAnimating) return@LaunchedEffect
        isAnimating = true
        for (i in animFrames.indices) {
            currentFrame = i
            delay(800L)
        }
        isOpened = true
        isAnimating = false
        onCookieOpened(cookieData.type)
    }

    val displayImage = when {
        isOpened -> getOpenedImage(cookieData.type)
        isAnimating -> animFrames.getOrElse(currentFrame) { cookieData.imgRes }
        else -> cookieData.imgRes
    }

    val message = run {
        val messages = cookieMessages[cookieData.type] ?: emptyList()
        val no = cookieData.no
        if (no != null && no > 0 && no <= messages.size) messages[no - 1] else ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBackground)
    ) {
        // Cookie image with pinch detection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (!isOpened && !isAnimating) {
                        Modifier.pointerInput(Unit) {
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
                                                val currentDist = hypot(
                                                    (pts[0].x - pts[1].x).toDouble(),
                                                    (pts[0].y - pts[1].y).toDouble()
                                                ).toFloat()
                                                if (currentDist - initialDistance > 200f) {
                                                    triggerAnimation = true
                                                    activePointers.clear()
                                                }
                                            }
                                        }
                                        PointerEventType.Release -> {
                                            event.changes.forEach { change ->
                                                activePointers.remove(change.id.value)
                                            }
                                            if (activePointers.size < 2) initialDistance = 0f
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
                    } else Modifier
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isOpened && message.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.img_message_paper),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.matchParentSize()
                        )
                        Text(
                            text = message,
                            color = MainBorder,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp, vertical = 24.dp)
                        )
                    }
                }

                Image(
                    painter = painterResource(displayImage),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                if (!isOpened && !isAnimating) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "양손으로 쿠키를 벌려보세요",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isOpened) {
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainButton,
                            contentColor = MainBorder
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(2.dp, MainBorder, RoundedCornerShape(8.dp))
                    ) {
                        Text("확인", fontWeight = FontWeight.Bold, color = MainBorder)
                    }
                }
            }
        }

        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "close",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
