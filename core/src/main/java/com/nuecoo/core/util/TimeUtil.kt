package com.nuecoo.core.util

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun getLocalTimeToString(): String {
    return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
}

fun nowAsYyyyMMddHHmm(): String =
    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))

fun todayAsYyyyMMdd(): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

fun getDayOfWeek(dateString: String, language: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    val dateTime = LocalDateTime.parse(dateString, formatter)
    val dayOfWeek: DayOfWeek = dateTime.dayOfWeek

    return when (language.lowercase()) {
        "en" -> when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Mon"
            DayOfWeek.TUESDAY -> "Tue"
            DayOfWeek.WEDNESDAY -> "Wed"
            DayOfWeek.THURSDAY -> "Thu"
            DayOfWeek.FRIDAY -> "Fri"
            DayOfWeek.SATURDAY -> "Sat"
            DayOfWeek.SUNDAY -> "Sun"
        }
        else -> when (dayOfWeek) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
        }
    }
}

fun formatDateWithDayOfWeek(input: String): String {
    val date = if (input.contains("_")) {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.KOREA)
        LocalDateTime.parse(input, formatter).toLocalDate()
    } else {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.KOREA)
        LocalDate.parse(input, formatter)
    }

    // 요일 구하기
    val dayOfWeek = when (date.dayOfWeek.value) {
        1 -> "월"
        2 -> "화"
        3 -> "수"
        4 -> "목"
        5 -> "금"
        6 -> "토"
        7 -> "일"
        else -> ""
    }

    // 출력 형식
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA)
    val formattedDate = date.format(outputFormatter)

    return "$formattedDate($dayOfWeek)"
}

fun addMonths(yyyyMM: String, monthsToAdd: Long): String {
    val date = yyyyMM.replace("-","").substring(0,6)
    val formatter = DateTimeFormatter.ofPattern("yyyyMM")
    val ym = YearMonth.parse(date, formatter)
    return ym.plusMonths(monthsToAdd).format(formatter)
}

fun diffMonths(yyyyMM1: String, yyyyMM2: String): Long {
    val date1 = yyyyMM1.replace("-","").substring(0,6)
    val date2 = yyyyMM2.replace("-","").substring(0,6)
    val formatter = DateTimeFormatter.ofPattern("yyyyMM")
    val ym1 = YearMonth.parse(date1, formatter)
    val ym2 = YearMonth.parse(date2, formatter)
    return ChronoUnit.MONTHS.between(ym2, ym1)
}