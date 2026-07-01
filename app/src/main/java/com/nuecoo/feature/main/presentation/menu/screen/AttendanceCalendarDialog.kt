package com.nuecoo.feature.main.presentation.menu.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nuecoo.R
import com.nuecoo.core.presetation.ui.component.DefaultItemBox
import com.nuecoo.core.theme.AttendanceComplete
import com.nuecoo.core.theme.CheckAttendance
import com.nuecoo.core.theme.CheckNonAttendance
import com.nuecoo.core.theme.MainText
import com.nuecoo.core.theme.MenuSubBoxBackground
import com.nuecoo.core.theme.SubText
import com.nuecoo.core.theme.UnknownColor
import com.nuecoo.core.theme.White
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AttendanceCalendarDialog(
    attendanceDates: Set<LocalDate>,
    onDismiss: () -> Unit
) {
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }

    Dialog(onDismissRequest = onDismiss) {
        DefaultItemBox {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.text_menu_attendance_calendar_title),
                    color = MainText,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )//다이얼로그 타이틀

                CalendarMonthHeader(
                    modifier = Modifier.padding(top = 20.dp),
                    yearMonth = displayedMonth,
                    onPrevMonth = { displayedMonth = displayedMonth.minusMonths(1) },
                    onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) }
                )//월 이동 헤더

                CalendarWeekDaysRow(modifier = Modifier.padding(top = 16.dp))//요일 라벨

                CalendarGrid(
                    modifier = Modifier.padding(top = 8.dp),
                    yearMonth = displayedMonth,
                    attendanceDates = attendanceDates
                )//날짜 그리드

                CalendarDialogClose(
                    modifier = Modifier.padding(top = 20.dp, bottom = 4.dp),
                    onDismiss = onDismiss
                )//닫기 버튼
            }
        }
    }
}

@Composable
private fun CalendarMonthHeader(
    modifier: Modifier,
    yearMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val canGoNext = yearMonth < YearMonth.now()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalendarMonthNavButton(iconRes = R.drawable.ic_back, onClick = onPrevMonth, enabled = true)

        Text(
            text = stringResource(
                R.string.text_menu_attendance_calendar_month,
                yearMonth.year,
                yearMonth.monthValue
            ),
            color = MainText,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        CalendarMonthNavButton(iconRes = R.drawable.ic_next, onClick = onNextMonth, enabled = canGoNext)
    }
}

@Composable
private fun CalendarMonthNavButton(iconRes: Int, onClick: () -> Unit, enabled: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MenuSubBoxBackground)
            .alpha(if (enabled) 1f else 0.4f)
            .clickable(onClick = onClick, enabled = enabled),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun CalendarWeekDaysRow(modifier: Modifier = Modifier) {
    val weekDays = LocalContext.current.resources.getStringArray(R.array.week_days)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEach { day ->
            Text(
                modifier = Modifier.width(36.dp),
                text = day,
                textAlign = TextAlign.Center,
                color = SubText,
                fontFamily = FontFamily(Font(R.font.title_font)),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CalendarGrid(modifier: Modifier, yearMonth: YearMonth, attendanceDates: Set<LocalDate>) {
    val leadingEmptyCount = yearMonth.atDay(1).dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()
    val trailingEmptyCount = (7 - (leadingEmptyCount + daysInMonth) % 7) % 7

    val cells: List<LocalDate?> = buildList {
        repeat(leadingEmptyCount) { add(null) }
        (1..daysInMonth).forEach { add(yearMonth.atDay(it)) }
        repeat(trailingEmptyCount) { add(null) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { date ->
                    CalendarDayCell(date = date, isAttendance = date != null && attendanceDates.contains(date))
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(date: LocalDate?, isAttendance: Boolean) {
    if (date == null) {
        Spacer(modifier = Modifier.size(36.dp))
        return
    }

    val isToday = date == LocalDate.now()

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isAttendance) CheckAttendance else CheckNonAttendance)
            .then(
                if (isToday) {
                    Modifier.border(1.5.dp, AttendanceComplete, RoundedCornerShape(10.dp))
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = if (isAttendance) White else UnknownColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CalendarDialogClose(modifier: Modifier, onDismiss: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onDismiss)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.close),
            color = SubText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}
