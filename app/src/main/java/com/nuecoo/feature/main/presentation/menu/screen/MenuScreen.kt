package com.nuecoo.feature.main.presentation.menu.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuecoo.R
import com.nuecoo.feature.main.presentation.main.component.MainTitleItem
import com.nuecoo.ui.theme.ItemCardBackground
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.MenuSubBoxBackground
import com.nuecoo.ui.theme.ProfileBackground
import com.nuecoo.ui.theme.ProfileBorder
import com.nuecoo.ui.theme.SubBackground
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.SubTitle
import com.nuecoo.viewmodel.CollectionProgress
import com.nuecoo.viewmodel.MenuViewModel

@Composable
fun MenuScreen(viewModel: MenuViewModel = hiltViewModel()) {
    val progress by viewModel.collectionProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    MenuScreenContent(
        progress = progress,
        isLoading = isLoading
    )
}

@Composable
private fun MenuScreenContent(
    progress: List<CollectionProgress>,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 24.dp)
    ) {
        MainTitleItem(
            subTitle = stringResource(R.string.text_menu_sub_title),
            mainTitle = stringResource(R.string.text_menu_title)
        )//메인 타이틀
        Spacer(Modifier.height(16.dp))
        ProfileItem()//프로필 뷰

        Spacer(Modifier.height(16.dp))
        CollectionProgressItem()
    }
}

@Composable
private fun ProfileItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(ItemCardBackground)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(ProfileBackground)
                    .border(
                        width = 3.dp,
                        color = ProfileBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.img_cookie_passion_1),//추후 데이터로 이미지 변경
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)

                )
            }
            Spacer(Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "이강현",//추후 데이터로 이름변경
                    color = MainText,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 2.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 14.dp)
                )
                Text(
                    text = "상태 메세지sfafawfwafajlfjwalfjwaljflawjflksajlkfjsaljfsalkjflksajflksajflkjsalkfjslakjflsajflksajflsjal",//추후 데이터로 상태 메세지 변경
                    color = SubText,
                    modifier = Modifier.padding(top = 6.dp),
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                )
            }
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 32.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MenuSubBoxBackground)
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = stringResource(R.string.text_menu_profile_edit),
                    color = SubText,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
private fun CollectionProgressItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(ItemCardBackground)
            .padding(horizontal = 28.dp, vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CookieCircleProgress(64, 12, 64)
        }

    }
}

@Composable
fun CookieCircleProgress(
    percent: Int,
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val arcSize = size.minDimension - strokeWidth

            drawArc(
                color = Color(0xFFEFE6D3),
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
                color = Color(0xFFD17834),
                startAngle = -90f,
                sweepAngle = 360f * (percent / 100f),
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(arcSize, arcSize),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = " $percent%",
                color = MainText,
                fontSize = 22.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.cookie_run_bold)),
            )

            Text(
                text = "$current/$total",
                color = SubTitle,
                fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                lineHeight = 10.sp
            )
        }
    }
}


/*@Composable
fun MenuScreena(
    rootNavController: NavController,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val progress by viewModel.collectionProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    var showCollectionDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadCollectionProgress() }

    val menuItems = listOf(
        Triple(R.drawable.img_cookie_cheering_1, "수집 현황", { showCollectionDialog = true }),
        Triple(R.drawable.img_message_paper, "앱 정보", {}),
        Triple(R.drawable.img_cookie_deactive, "설정", { showLogoutDialog = true })
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        menuItems.forEach { (iconRes, label, onClick) ->
            android.view.MenuItem(iconRes = iconRes, label = label, onClick = onClick)
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "v 1.0",
            color = MainBorder,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
    }

    if (showCollectionDialog) {
        CollectionProgressDialog(
            progress = progress,
            isLoading = isLoading,
            onDismiss = { showCollectionDialog = false }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("설정", color = MainBorder, fontWeight = FontWeight.Bold) },
            text = { Text("로그아웃 하시겠습니까?", color = MainBorder) },
            containerColor = SubBackground,
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.logout()
                        showLogoutDialog = false
                        rootNavController.navigate(Route.LOGIN) {
                            popUpTo(Route.MAIN) { inclusive = true }
                        }
                    }
                }) {
                    Text("로그아웃", color = MainBorder, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("취소", color = MainBorder)
                }
            }
        )
    }
}*/

@Composable
private fun MenuItem(iconRes: Int, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            color = MainBorder,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CollectionProgressDialog(
    progress: List<CollectionProgress>,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    val cookieImages = listOf(
        R.drawable.img_cookie_cheering_1,
        R.drawable.img_cookie_comfort_1,
        R.drawable.img_cookie_passion_1,
        R.drawable.img_cookie_sermon_1
    )
    val cookieColors = listOf(
        Color(0xFFFFB347), Color(0xFF87CEEB), Color(0xFFFF6B6B), Color(0xFF90EE90)
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SubBackground)
                .border(4.dp, MainBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                "수집 현황",
                color = MainBorder,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    color = MainBorder,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                progress.forEachIndexed { index, (collected, total) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MainBackground)
                                .border(2.dp, MainBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(cookieImages.getOrElse(index) { R.drawable.img_cookie_deactive }),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MainBorder.copy(alpha = 0.1f))
                            ) {
                                val ratio = if (total > 0) collected.toFloat() / total else 0f
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(cookieColors.getOrElse(index) { MainButton })
                                )
                                Text(
                                    "$collected/$total",
                                    color = MainBorder,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("확인", color = MainBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
