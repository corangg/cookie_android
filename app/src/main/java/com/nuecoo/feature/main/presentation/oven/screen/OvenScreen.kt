package com.nuecoo.feature.main.presentation.oven.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuecoo.R
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.feature.main.presentation.main.component.MainTitleItem
import com.nuecoo.feature.main.presentation.oven.viewmodel.OvenViewModel
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.MainTitle
import com.nuecoo.ui.theme.ItemCardBackground
import com.nuecoo.ui.theme.NueCooTheme
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.White
import getCookieMessageResMap
import getCookieTypeListSize
import kotlinx.coroutines.launch
import toUiItem

@Composable
fun OvenScreen(viewModel: OvenViewModel = hiltViewModel(), onMoveCollection: () -> Unit) {
    val context = LocalContext.current
    val dailyCookieData by viewModel.dailyCookieData.collectAsStateWithLifecycle()
    val notOpenedCookies by viewModel.notOpenedCookies.collectAsStateWithLifecycle()
    val remainTime by viewModel.remainTime.collectAsState()
    val selectedCookie by viewModel.selectedCookie.collectAsState()

    val cookieMessageResMap = getCookieMessageResMap()

    val cookieNameMap = cookieMessageResMap.mapValues { entry ->
        stringArrayResource(entry.value).toList()
    }

    LaunchedEffect(Unit) {
        viewModel.initDailyCookie(context.getCookieTypeListSize())
    }

    OvenScreenContent(
        remainTime = remainTime,
        cookieList = dailyCookieData?.list?.map { it.toUiItem() } ?: emptyList(),
        notOpenedCookies = notOpenedCookies,
        selectedCookie = selectedCookie,
        cookieNameMap = cookieNameMap,
        onCookieClick = { uiItem ->
            val domainItem = dailyCookieData?.list?.find { it.type == uiItem.type }
            if (domainItem?.isOpened != null) {
                viewModel.selectCookie(uiItem)
            }
        },
        onCookieClose = { viewModel.clearSelectedCookie() },
        onCookieOpened = { type ->
            viewModel.updateOpenCookieData(
                type,
                size = cookieNameMap[type]?.size ?: 0
            )
        },
        onMoveCollection = onMoveCollection
    )
}

@Composable
private fun OvenScreenContent(
    remainTime: String,
    cookieList: List<CookieUIItemData>,
    notOpenedCookies: Int,
    selectedCookie: CookieUIItemData?,
    cookieNameMap: Map<Int, List<String>>,
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
            openCookie = onCookieClick
        )//쿠키 트레이

        CookieBottomMessage()//쿠키 하단 메세지

        selectedCookie?.let { cookie ->
            showOpenedCookie(
                item = cookie,
                cookieNameMap = cookieNameMap,
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
    list: List<CookieUIItemData>,
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
                    onClick = { openCookie(item) }
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
    cookieNameMap: Map<Int, List<String>>,
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
            cookieMessages = cookieNameMap,
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
fun CookieItem(data: CookieUIItemData, onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    var showFullDialog by remember { mutableStateOf(false) }

    if (showFullDialog) {
        AllCookieOpenedDialog(onDismiss = { showFullDialog = false })
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
private fun AllCookieOpenedDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(ItemCardBackground)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.text_toast_cookie_all_collect),
                color = MainText,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MainButton)
                    .clickable(onClick = onDismiss)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.btn_ok),
                    color = White,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "Oven Screen",
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun OvenScreenPreview() {
    NueCooTheme {
        OvenScreenContent(
            remainTime = "08 : 30 : 00",
            cookieList = listOf(
                CookieUIItemData(
                    type = CookieType.Cheering.type,
                    isOpened = false,
                    imgRes = R.drawable.img_cookie_cheering_1
                ),
                CookieUIItemData(
                    type = CookieType.Comfort.type,
                    isOpened = true,
                    imgRes = R.drawable.img_cookie_comfort_6
                ),
                CookieUIItemData(
                    type = CookieType.Passion.type,
                    isOpened = false,
                    imgRes = R.drawable.img_cookie_passion_1
                ),
                CookieUIItemData(
                    type = CookieType.Sermon.type,
                    isOpened = null,
                    imgRes = R.drawable.img_cookie_deactive
                ),
            ),
            selectedCookie = null,
            cookieNameMap = emptyMap(),
            onCookieClick = {},
            onCookieClose = {},
            onCookieOpened = {},
            onMoveCollection = {},
            notOpenedCookies = 1
        )
    }
}
