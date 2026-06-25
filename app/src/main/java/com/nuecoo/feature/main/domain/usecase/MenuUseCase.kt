package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
import com.nuecoo.feature.main.domain.model.WeeklyAttendanceModel
import com.nuecoo.feature.main.domain.repository.CookieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetAttendanceCount @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getFlowCookieDataList().map { itemData ->
            val list = itemData.map { it.date }
            getAttendanceStreak(list)
        }
    }

    private fun getAttendanceStreak(dateList: List<String>): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        val attendanceDates = dateList
            .mapNotNull { date ->
                runCatching {
                    LocalDate.parse(date, formatter)
                }.getOrNull()
            }
            .toSet()

        val today = LocalDate.now()

        val startDate = when {
            attendanceDates.contains(today) -> today
            attendanceDates.contains(today.minusDays(1)) -> today.minusDays(1)
            else -> return 0
        }

        var streak = 0
        var currentDate = startDate

        while (attendanceDates.contains(currentDate)) {
            streak++
            currentDate = currentDate.minusDays(1)
        }

        return streak
    }
}

class CheckTodayAttendance @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.getFlowDailyCookieData().map { data ->
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            if (data == null || data.date != today) return@map false
            data.list.any { it.no != null }
        }
    }
}

class GetWeeklyAttendance @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<List<WeeklyAttendanceModel>> {
        return repository.getFlowCookieDataList().map { data ->
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val today = LocalDate.now()
            val sunday = today.minusDays(today.dayOfWeek.value % 7L)

            (0..6).map { index ->
                val date = sunday.plusDays(index.toLong()).format(formatter)

                WeeklyAttendanceModel(
                    dayIndex = index,
                    isAttendance = date.checkAttendance(data)
                )
            }
        }
    }

    private fun String.checkAttendance(list: List<DailyCookieItemData>): Boolean {
        val data = list.find { it.date == this } ?: return false
        return data.list.any { it.no != null }
    }
}

class LogOutUseCase @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke() = repository.logOut()
}