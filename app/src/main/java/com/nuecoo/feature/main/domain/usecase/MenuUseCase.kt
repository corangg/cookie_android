package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.core.data.datasource.remote.FirebaseDataDataSource
import com.nuecoo.feature.auth.domain.AuthRepository
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
        return repository.observeDailyClaimDates().map { dates ->
            getAttendanceStreak(dates)
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

class CheckTodayAttendanceUseCase @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.observeDailyClaimDates().map { dates ->
        dates.contains(todayAsString())
    }

    private fun todayAsString(): String = LocalDate.now().format(FORMATTER)

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}

class GetWeeklyAttendanceUseCase @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<List<WeeklyAttendanceModel>> {
        return repository.observeDailyClaimDates().map { dates->
            val attendanceDates = dates.toSet()
            val today = LocalDate.now()
            val sunday = today.minusDays(today.dayOfWeek.value % 7L)
            (0..6).map { index ->
                val date = sunday.plusDays(index.toLong())
                WeeklyAttendanceModel(
                    dayIndex = index,
                    isAttendance = attendanceDates.contains(date.format(FORMATTER)
                    )
                )
            }
        }
    }
    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}

class GetAttendanceDatesUseCase @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<Set<LocalDate>> {
        return repository.observeDailyClaimDates().map { dates ->
            dates.mapNotNull { runCatching { LocalDate.parse(it, FORMATTER) }.getOrNull() }.toSet()
        }
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}

class LogOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logOut()
}

class RefreshUserInfoUseCase @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke() = repository.refreshUserInfo()
}

class ObserveUserInfoUseCase @Inject constructor(
    private val repository: AuthRepository
){
    operator fun invoke() = repository.observeUserInfo()
}
