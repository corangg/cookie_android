package com.nuecoo.feature.main.presentation.collection.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.core.util.toDateUnit
import com.nuecoo.feature.auth.domain.model.CollectionDisplayItem
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.feature.main.presentation.oven.screen.CookieOpenScreen
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.NueCooTheme
import getCookieTypeColor
import getCookieTypeMainTextRes
import kotlin.math.hypot

@Composable
fun CollectionOpenScreen(
    collectionData: CollectionDisplayItem,
    imgRes: Int,
    message: String,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        MainCollectionView(
            collectionData = collectionData,
            imgRes = imgRes,
            message = message
        )//메인 뷰

        CloseButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp)
                .padding(end = 12.dp),
            onClose = onClose
        )//닫기 버튼
    }
}

@Composable
private fun MainCollectionView(
    collectionData: CollectionDisplayItem,
    imgRes: Int,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = (-36).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SetTypeMessage(collectionData)//상단 메세지
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(imgRes),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
                .padding(top = 24.dp)
        )//이미지

        MessageBackgroundBox(message = message)//쿠키 메세지
    }
}

@Composable
private fun CloseButton(modifier: Modifier, onClose: () -> Unit) {
    IconButton(
        onClick = onClose,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.ic_close_white),
            contentDescription = "close",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun SetTypeMessage(data: CollectionDisplayItem) {
    val typeColor = getCookieTypeColor(data.type)
    val context = LocalContext.current
    val dateText = data.date?.toDateUnit(context) ?: ""
    val main =
        "${dateText}${stringResource(R.string.of)} ${stringResource(getCookieTypeMainTextRes(data.type))}"
    val sub = "${stringResource(R.string.no)}.${data.no}"
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
            onCookieOpened = {},
            onMoveCollection = {}
        )
    }
}