package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.main.domain.model.WeeklyAttendanceModel
import com.nuecoo.feature.main.domain.model.isSaved
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
        return repository.observeAllEvents().map { events ->
            val attendanceDates = events
                .filter { it.isSaved }
                .map { it.claimDate }
                .toSet()
            getAttendanceStreak(attendanceDates.toList())
        }
    }

    private fun getAttendanceStreak(dateList: List<String>): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val attendanceDates = dateList
            .mapNotNull { runCatching { LocalDate.parse(it, formatter) }.getOrNull() }
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
    operator fun invoke(): Flow<Boolean> =
        repository.observeEventsForToday().map { events -> events.any { it.isSaved } }
}

class GetWeeklyAttendance @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<List<WeeklyAttendanceModel>> {
        return repository.observeAllEvents().map { events ->
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val today = LocalDate.now()
            val sunday = today.minusDays(today.dayOfWeek.value % 7L)
            val savedDates = events.filter { it.isSaved }.map { it.claimDate }.toSet()

            (0..6).map { index ->
                val date = sunday.plusDays(index.toLong()).format(formatter)
                WeeklyAttendanceModel(dayIndex = index, isAttendance = savedDates.contains(date))
            }
        }
    }
}

class LogOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logOut()
}
