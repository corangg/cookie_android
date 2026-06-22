package com.nuecoo.feature.main.presentation.menu.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuecoo.BuildConfig
import com.nuecoo.R
import com.nuecoo.core.ui.component.DefaultItemBox
import com.nuecoo.feature.main.domain.model.WeeklyAttendanceModel
import com.nuecoo.feature.main.presentation.main.component.MainTitleItem
import com.nuecoo.ui.theme.AttendanceActive
import com.nuecoo.ui.theme.AttendanceComplete
import com.nuecoo.ui.theme.AttendanceInActive
import com.nuecoo.ui.theme.CheckAttendance
import com.nuecoo.ui.theme.CheckNonAttendance
import com.nuecoo.ui.theme.DefaultIconBackground
import com.nuecoo.ui.theme.LogoutBackground
import com.nuecoo.ui.theme.LogoutText
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainProgress
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.MenuSubBoxBackground
import com.nuecoo.ui.theme.ProfileBackground
import com.nuecoo.ui.theme.ProfileBorder
import com.nuecoo.ui.theme.ProgressBackground
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.SubTitle
import com.nuecoo.ui.theme.UnknownColor
import com.nuecoo.ui.theme.White
import com.nuecoo.ui.theme.WidgetActive
import com.nuecoo.ui.theme.WidgetActiveBackground
import com.nuecoo.ui.theme.WidgetInActive
import com.nuecoo.ui.theme.WidgetInActiveBackground
import com.nuecoo.ui.theme.WidgetOff
import com.nuecoo.ui.theme.WidgetOn
import com.nuecoo.feature.main.presentation.menu.viewmodel.CollectionProgress
import com.nuecoo.feature.main.presentation.menu.viewmodel.MenuViewModel
import getCookieTypeColor
import getCookieTypeList
import getCookieTypeListSize

@Composable
fun MenuScreen(
    viewModel: MenuViewModel = hiltViewModel(),
    onMoveOven: () -> Unit,
    onMoveAppInfo: () -> Unit,
) {
    val context = LocalContext.current
    val progress by viewModel.collectionProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val attendanceCount by viewModel.attendanceCount.collectAsStateWithLifecycle()
    val isTodayAttendance by viewModel.isTodayAttendance.collectAsStateWithLifecycle()
    val weeklyAttendance by viewModel.weeklyAttendance.collectAsStateWithLifecycle()
    val widgetEnabled by viewModel.widgetEnabled.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadCollectionProgress(context.getCookieTypeListSize())
    }

    MenuScreenContent(
        progress = progress,
        isLoading = isLoading,
        attendanceCount = attendanceCount ?: 0,
        isTodayAttendance = isTodayAttendance,
        weeklyAttendance = weeklyAttendance,
        isWidgetEnabled = widgetEnabled,
        onMoveOven = onMoveOven,
        onMoveAppInfo = onMoveAppInfo,
        onSaveWidgetEnabled = {
            viewModel.saveWidgetEnabled(it)
        }
    )
}

@Composable
private fun MenuScreenContent(
    progress: List<CollectionProgress>,
    isLoading: Boolean,
    attendanceCount: Int,
    isTodayAttendance: Boolean,
    weeklyAttendance: List<WeeklyAttendanceModel>,
    isWidgetEnabled: Boolean,
    onMoveOven: () -> Unit,
    onMoveAppInfo: () -> Unit,
    onSaveWidgetEnabled: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        MainTitleItem(
            modifier = Modifier.padding(top = 16.dp),
            subTitle = stringResource(R.string.text_menu_sub_title),
            mainTitle = stringResource(R.string.text_menu_title)
        )//메인 타이틀

        ProfileItem(modifier = Modifier.padding(top = 16.dp))//프로필 컴포넌트

        CheckInDayItem(
            modifier = Modifier.padding(top = 16.dp),
            attendanceCount = attendanceCount,
            isTodayAttendance = isTodayAttendance,
            onClick = onMoveOven,
            weeklyAttendance = weeklyAttendance
        )//출석체크 컴포넌트

        CollectionProgressItem(
            modifier = Modifier.padding(top = 16.dp),
            progressList = progress
        )//콜랙션 컴포넌트

        WidgetItem(
            modifier = Modifier.padding(top = 16.dp),
            isWidget = isWidgetEnabled,
            onSaveWidgetEnabled = onSaveWidgetEnabled
        )//위젯 컴포넌트

        AppInfoItem(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onMoveAppInfo
        )//앱 정보 컴포넌트

        LogOutItem(
            modifier = Modifier.padding(top = 16.dp)
        )//로그아웃 컴포넌트

        AppVersionItem(
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))//앱 버전 컴포넌트
    }
}

@Composable
private fun ProfileItem(modifier: Modifier) {
    DefaultItemBox(modifier = modifier) {
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
private fun CheckInDayItem(
    modifier: Modifier,
    attendanceCount: Int,
    isTodayAttendance: Boolean,
    weeklyAttendance: List<WeeklyAttendanceModel>,
    onClick: () -> Unit
) {
    DefaultItemBox(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AttendanceStreakText(attendanceCount = attendanceCount)//연속 출석일 수 컴포넌트
                Spacer(modifier = Modifier.weight(1f))
                AttendanceCard(
                    modifier = Modifier.padding(start = 16.dp),
                    isAttended = isTodayAttendance,
                    onClick = onClick
                )//출석 여부 표시
            }
            WeeklyAttendanceCard(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                list = weeklyAttendance
            )//주간 출석 컴포넌트
        }
    }
}

@Composable
private fun AttendanceStreakText(attendanceCount: Int) {
    val isAttended = (attendanceCount != 0)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isAttended) AttendanceActive else AttendanceInActive),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(if (isAttended) R.drawable.ic_attendance_active else R.drawable.ic_attendance_in_active),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)

            )
        }

        Text(
            modifier = Modifier.padding(start = 8.dp),
            color = MainText,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontSize = 16.sp,
            text = "${attendanceCount}${stringResource(R.string.text_menu_attendance)}"
        )
    }
}

@Composable
private fun AttendanceCard(modifier: Modifier, isAttended: Boolean, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(width = 90.dp, height = 28.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isAttended) AttendanceActive else AttendanceInActive)
            .clickable(onClick = onClick, enabled = !isAttended)
    ) {
        Text(
            text = stringResource(if (isAttended) R.string.text_menu_attendance_complete else R.string.text_menu_attend),
            color = if (isAttended) AttendanceComplete else White,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun WeeklyAttendanceCard(modifier: Modifier, list: List<WeeklyAttendanceModel>) {
    val weekDays = LocalContext.current.resources.getStringArray(R.array.week_days)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        list.forEach {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (it.isAttendance) CheckAttendance else CheckNonAttendance),
                    contentAlignment = Alignment.Center
                ) {
                    if (it.isAttendance) {
                        Image(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "-",
                            color = UnknownColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(top = 6.dp),
                    text = weekDays[it.dayIndex],
                    color = SubText,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CollectionProgressItem(modifier: Modifier, progressList: List<CollectionProgress>) {
    val total = progressList.sumOf { it.total }
    val colleted = progressList.sumOf { it.collected }

    DefaultItemBox(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CookieCircleProgress(colleted, total)//전체 쿠키 수집률 컴포넌트

            CollectionList(
                modifier = Modifier.padding(start = 20.dp),
                progressList = progressList
            )//각 쿠키 수집률 컴포넌트
        }
    }
}

@Composable
fun CookieCircleProgress(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val percent = (current.toFloat() / total.toFloat() * 100).toInt()
    val animatedProgress by animateFloatAsState(
        targetValue = percent / 100f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "circleProgressAnimation"
    )

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
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
                color = MainProgress,
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

@Composable
private fun CollectionList(modifier: Modifier, progressList: List<CollectionProgress>) {
    Column(modifier = modifier) {
        val cookies = getCookieTypeList()
        cookies.forEach {
            val data =
                progressList.find { progress -> progress.type == it.type.type } ?: return@forEach
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(it.nameRes),
                    color = MainText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )//개별 콜랙션 타입 명

                CollectionProgressLine(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    progressData = data
                )//직선 프로그래스 컴포넌트

                CollectionProgressText(
                    modifier = Modifier.padding(start = 12.dp),
                    progressData = data
                )//개별 콜랙션 텍스트 컴포넌트
            }
        }
    }
}

@Composable
private fun CollectionProgressLine(modifier: Modifier, progressData: CollectionProgress) {
    val progress = progressData.collected.toFloat() / progressData.total.toFloat()

    Canvas(
        modifier = modifier
            .height(10.dp)
            .fillMaxWidth()
    ) {
        val strokeWidth = size.height
        val radius = strokeWidth / 2

        drawLine(
            color = ProgressBackground,
            start = Offset(radius, center.y),
            end = Offset(size.width - radius, center.y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = getCookieTypeColor(progressData.type),
            start = Offset(radius, center.y),
            end = Offset(
                x = radius + (size.width - strokeWidth) * progress.coerceIn(0f, 1f),
                y = center.y
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun CollectionProgressText(modifier: Modifier, progressData: CollectionProgress) {
    val colleted = progressData.collected
    val total = progressData.total
    Text(
        modifier = modifier,
        text = "${colleted}/${total}",
        color = SubText,
        fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
}

@Composable
private fun WidgetItem(
    modifier: Modifier,
    isWidget: Boolean,
    onSaveWidgetEnabled: (Boolean) -> Unit
) {
    DefaultItemBox(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WidgetIcon(isWidget)//위젯 아이콘
            WidgetText(isWidget)//위젯 텍스트
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                modifier = Modifier.scale(1.15f),
                checked = isWidget,
                onCheckedChange = { checked ->
                    onSaveWidgetEnabled(checked)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = WidgetOn,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = WidgetOff,
                    uncheckedBorderColor = Color.Transparent,
                    checkedBorderColor = Color.Transparent
                ),
            )//위젯 스위치
        }
    }
}

@Composable
private fun WidgetIcon(isWidget: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isWidget) WidgetActiveBackground else WidgetInActiveBackground),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_widget),
            colorFilter = if(isWidget) ColorFilter.tint(WidgetActive) else ColorFilter.tint(WidgetInActive),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun WidgetText(isWidget: Boolean) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(
            text = stringResource(R.string.text_menu_widget),
            color = MainText,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 16.sp
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = stringResource(if (isWidget) R.string.text_menu_widget_on else R.string.text_menu_widget_off),
            color = SubText,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 12.sp
        )
    }
}

@Composable
private fun AppInfoItem(modifier: Modifier, onClick: () -> Unit) {
    DefaultItemBox(modifier = modifier, onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DefaultIconBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_app_info),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(R.string.text_menu_app_info),
                color = MainText,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.ic_next),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
            )
        }
    }
}

@Composable
private fun LogOutItem(modifier: Modifier){
    DefaultItemBox(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LogoutBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(R.string.text_menu_app_logout),
                color = LogoutText,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.ic_next),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun AppVersionItem(modifier: Modifier){
    Text(
        modifier = modifier.fillMaxWidth(),
        text = "${stringResource(R.string.app_name)} · ${BuildConfig.VERSION_NAME}",
        color = SubText,
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(R.font.title_font)),
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
}