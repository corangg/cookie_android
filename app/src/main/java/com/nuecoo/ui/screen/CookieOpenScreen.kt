package com.nuecoo.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.domain.model.CookieType
import com.nuecoo.domain.model.CookieUIItemData
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.NueCooTheme
import kotlinx.coroutines.delay
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
                if (isOpened && message.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
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
                                color = Color(0xFF5C8DCC),
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "응원",
                        color = Color(0xFF5C8DCC),
                        fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "힘을 주는 한마디",
                        color = Color(0xFF7A6E63),
                        fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
                        fontWeight = FontWeight.Thin,
                        fontSize = 13.sp
                    )
                }

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

                Spacer(Modifier.height(72.dp))

                if (!isOpened && !isAnimating) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        stringResource(R.string.text_open_cookie_sub),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
                        fontWeight = FontWeight.Thin,
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

private fun Modifier.cookiePinchOpenDetector(
    enabled: Boolean,
    onOpen: () -> Unit
): Modifier {
    return if (!enabled) this
    else this.pointerInput(Unit) {
        val activePointers = mutableMapOf<Long, Offset>()
        var initialDistance = 0f

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

                            if (currentDistance - initialDistance > 200f) {
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

@Preview(
    showBackground = true,
    name = "Cookie Open Screen - Closed",
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun CookieOpenScreenClosedPreview() {
    NueCooTheme {
        CookieOpenScreen(
            cookieData = CookieUIItemData(
                type = CookieType.Cheering.type,
                no = 1,
                isOpened = false,
                imgRes = R.drawable.img_cookie_cheering_1
            ),
            cookieMessages = mapOf(
                CookieType.Cheering.type to listOf(
                    "오늘도 충분히 잘하고 있어요!"
                )
            ),
            onClose = {},
            onCookieOpened = {}
        )
    }
}