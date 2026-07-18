package com.nuecoo.feature.main.presentation.oven.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuecoo.R
import com.nuecoo.core.presetation.ui.component.DefaultItemBox
import com.nuecoo.core.presetation.ui.component.MainTitleItem
import com.nuecoo.core.theme.MainBackground
import com.nuecoo.core.theme.MainBorder
import com.nuecoo.core.theme.MainText
import com.nuecoo.core.theme.MainTitle
import com.nuecoo.core.theme.ProgressBackground
import com.nuecoo.core.theme.SubText
import com.nuecoo.core.theme.SubTitle
import com.nuecoo.core.theme.White
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.feature.main.domain.model.TypeCollectedCount
import com.nuecoo.feature.main.presentation.oven.viewmodel.OvenViewModel
import getCookieTypeAllCollectedTextRes
import getCookieTypeBackgroundColor
import getCookieTypeColor
import getCookieTypeMainTextRes
import kotlinx.coroutines.launch

@Composable
fun OvenScreen(viewModel: OvenViewModel = hiltViewModel(), onMoveCollection: () -> Unit) {
    val cookieSlotItems by viewModel.cookieSlotItems.collectAsStateWithLifecycle()
    val notOpenedCookies by viewModel.notOpenedCookies.collectAsStateWithLifecycle()
    val remainTime by viewModel.remainTime.collectAsStateWithLifecycle()
    val selectedCookie by viewModel.selectedCookie.collectAsStateWithLifecycle()
    val cookieCount by viewModel.collectionProgress.collectAsStateWithLifecycle()

    OvenScreenContent(
        remainTime = remainTime,
        cookieList = cookieSlotItems,
        notOpenedCookies = notOpenedCookies,
        selectedCookie = selectedCookie,
        cookieCount = cookieCount,
        onCookieClick = { uiItem ->
            viewModel.selectCookie(uiItem.type)
        },
        onCookieClose = { viewModel.clearSelectedCookie() },
        onCookieOpened = { type ->
            viewModel.openCookie(type)
        },
        onMoveCollection = onMoveCollection
    )
}

@Composable
private fun OvenScreenContent(
    remainTime: String,
    cookieList: List<CookieUIItemData>,
    notOpenedCookies: Int,
    cookieCount: List<TypeCollectedCount>,
    selectedCookie: CookieUIItemData?,
    onCookieClick: (CookieUIItemData) -> Unit,
    onCookieClose: () -> Unit,
    onCookieOpened: (Int) -> Unit,
    onMoveCollection: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        MainTitleItem(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            subTitle = stringResource(R.string.text_oven_sub_title),
            mainTitle = stringResource(R.string.text_oven_title)
        )//메인 타이틀

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LeftoverCookie(value = notOpenedCookies)//남은 쿠키
            TimerInitCookie(remainTime)//쿠키 초기화 시간
        }

        CookieTray(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp),
            list = cookieList,
            onMoveCollection = onMoveCollection,
            openCookie = onCookieClick,
            totalCount = cookieCount
        )//쿠키 트레이

        CookieBottomMessage()//쿠키 하단 메세지

        selectedCookie?.let { cookie ->
            showOpenedCookie(
                item = cookie,
                onCookieOpened = onCookieOpened,
                onMoveCollection = onMoveCollection,
                onClose = onCookieClose
            )//쿠키 오픈 다이얼로그
        }
    }
}

@Composable
private fun LeftoverCookie(value: Int) {
    Text(
        text = "${stringResource(R.string.text_oven_title)} ${value}${stringResource(R.string.text_oven_sub_unit)}",
        color = MainTitle,
        fontSize = 13.sp,
        fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
        fontWeight = FontWeight.Thin,
        modifier = Modifier.padding(start = 28.dp)
    )
}

@Composable
private fun TimerInitCookie(time: String) {
    Text(
        text = time,
        color = MainBorder,
        fontSize = 13.sp,
        fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
        modifier = Modifier.padding(end = 28.dp)
    )
}

@Composable
private fun CookieTray(
    modifier: Modifier,
    totalCount: List<TypeCollectedCount>,
    list: List<CookieUIItemData>,
    onMoveCollection: () -> Unit,
    openCookie: (CookieUIItemData) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.img_tray),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 80.dp),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            items(list, key = { it.type }) { item ->
                CookieItem(
                    data = item,
                    onMoveCollection = onMoveCollection,
                    onClick = { openCookie(item) },
                    totalCount =  totalCount.find { it.type == item.type }?.maxCount?:return@items
                )
            }
        }
    }
}

@Composable
private fun CookieBottomMessage() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.text_oven_tray),
        color = MainTitle,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
        fontWeight = FontWeight.Thin
    )
}

@Composable
private fun showOpenedCookie(
    item: CookieUIItemData,
    onCookieOpened: (Int) -> Unit,
    onMoveCollection: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CookieOpenScreen(
            cookieData = item,
            onClose = onClose,
            onCookieOpened = onCookieOpened,
            onMoveCollection = {
                onClose()
                onMoveCollection()
            }
        )
    }
}

@Composable
fun CookieItem(
    data: CookieUIItemData,
    totalCount: Int,
    onMoveCollection: () -> Unit,
    onClick: () -> Unit,
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    var showFullDialog by remember { mutableStateOf(false) }

    if (showFullDialog) {
        AllCookieOpenedDialog(
            type = data.type,
            onMoveCollection = onMoveCollection,
            onDismiss = { showFullDialog = false },
            totalCount = totalCount)
    }

    Image(
        painter = painterResource(data.imgRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .wrapContentSize()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(targetValue = 0.9f, animationSpec = tween(80))
                    scale.animateTo(targetValue = 1f, animationSpec = tween(120))
                    if (data.isFull) {
                        showFullDialog = true
                    } else {
                        onClick()
                    }
                }
            }
    )
}

@Composable
private fun AllCookieOpenedDialog(type: Int, totalCount: Int, onMoveCollection: () -> Unit, onDismiss: () -> Unit) {
    val defaultColor = getCookieTypeColor(type)
    val backgroundColor = getCookieTypeBackgroundColor(type)
    val cookieText = stringResource(getCookieTypeMainTextRes(type))
    val allCollectedText = stringResource(getCookieTypeAllCollectedTextRes(type))

    Dialog(onDismissRequest = onDismiss) {
        DefaultItemBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                DialogCard(
                    modifier = Modifier.padding(top = 10.dp),
                    mainColor = defaultColor,
                    subColor = backgroundColor,
                    title = allCollectedText
                )//상단 타이틀 카드

                CookieCircleProgress(
                    color = defaultColor,
                    total = totalCount,
                    modifier = Modifier.padding(top = 20.dp)
                )//원형 프로그래스

                DialogTitle(
                    modifier = Modifier.padding(top = 20.dp),
                    color = defaultColor,
                    text = cookieText,
                )//다이얼로그 타이틀

                DialogSub(
                    modifier = Modifier.padding(top = 16.dp),
                    text = cookieText,
                )//다이얼로그 바디

                DialogBottomCard(
                    modifier = Modifier.padding(top = 20.dp),
                    mainColor = defaultColor,
                    subColor = backgroundColor
                )//하단 카드

                DialogCollectionButton(
                    modifier = Modifier.padding(top = 24.dp),
                    onMove = onMoveCollection,
                    onDismiss = onDismiss,
                    color = defaultColor
                )//콜렉션 이동 버튼

                DialogClose(
                    modifier = Modifier.padding(top = 16.dp, bottom = 20.dp),
                    onDismiss = onDismiss
                )//닫기 버튼
            }
        }
    }
}

@Composable
private fun DialogCard(modifier: Modifier, mainColor: Color, subColor: Color, title: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(subColor)
            .padding(vertical = 4.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = mainColor,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CookieCircleProgress(
    color: Color,
    total: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "circleProgressAnimation"
    )

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 9.dp.toPx()
            val arcSize = size.minDimension - strokeWidth

            drawArc(
                color = ProgressBackground,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(arcSize, arcSize),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(arcSize, arcSize),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$total",
                color = color,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.cookie_run_bold)),
            )

            Text(
                text = "/$total",
                color = SubTitle,
                fontFamily = FontFamily(Font(R.font.cookie_run_bold)),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun DialogTitle(modifier: Modifier, color: Color, text: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = color,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.text_open_all_colleted_main),
            color = MainText,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DialogSub(modifier: Modifier, text: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "$text ${stringResource(R.string.text_open_all_colleted_sub)}",
            color = SubText,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DialogBottomCard(modifier: Modifier, mainColor: Color, subColor: Color) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(subColor)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.text_open_all_colleted_cookie_continue),
            color = mainColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DialogCollectionButton(
    modifier: Modifier,
    onMove: () -> Unit,
    onDismiss: () -> Unit,
    color: Color
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .clickable(onClick = {
                //onDismiss()
                onMove()
            })
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.text_open_all_colleted_move_collection),
            color = White,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DialogClose(
    modifier: Modifier,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onDismiss)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(R.string.close),
            color = SubText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}