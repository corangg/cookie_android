package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.main.domain.model.CookieEvent
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.domain.repository.CookieRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val fmt = DateTimeFormatter.ofPattern("yyyyMMdd")
private fun today() = LocalDate.now().format(fmt)
private fun daysAgo(n: Long) = LocalDate.now().minusDays(n).format(fmt)

private fun savedEventOnDate(date: String) =
    CookieEvent("id_$date", "${date}1000", date, 0, 1, CookieSyncStatus.SAVED)

private fun pendingEventOnDate(date: String) =
    CookieEvent("id_pending_$date", "${date}1000", date, 0, null, CookieSyncStatus.PENDING)

// ──────────────────────────────────────────────
// GetAttendanceCount
// ──────────────────────────────────────────────

class GetAttendanceCountTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: GetAttendanceCount

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetAttendanceCount(repository)
    }

    private fun givenSavedDates(vararg dates: String) {
        every { repository.observeAllEvents() } returns flowOf(dates.map { savedEventOnDate(it) })
    }

    @Test
    fun `출석 기록이 없으면 스트릭은 0이다`() = runTest {
        givenSavedDates()
        assertEquals(0, useCase().first())
    }

    @Test
    fun `오늘만 출석하면 스트릭은 1이다`() = runTest {
        givenSavedDates(today())
        assertEquals(1, useCase().first())
    }

    @Test
    fun `어제만 출석하면 스트릭은 1이다`() = runTest {
        givenSavedDates(daysAgo(1))
        assertEquals(1, useCase().first())
    }

    @Test
    fun `이틀 전만 출석하면 스트릭은 0이다`() = runTest {
        givenSavedDates(daysAgo(2))
        assertEquals(0, useCase().first())
    }

    @Test
    fun `오늘과 어제 모두 출석하면 스트릭은 2이다`() = runTest {
        givenSavedDates(today(), daysAgo(1))
        assertEquals(2, useCase().first())
    }

    @Test
    fun `오늘 포함 3일 연속 출석하면 스트릭은 3이다`() = runTest {
        givenSavedDates(today(), daysAgo(1), daysAgo(2))
        assertEquals(3, useCase().first())
    }

    @Test
    fun `오늘은 있지만 어제가 없으면 스트릭은 1이다`() = runTest {
        givenSavedDates(today(), daysAgo(2))
        assertEquals(1, useCase().first())
    }
}

// ──────────────────────────────────────────────
// CheckTodayAttendance
// ──────────────────────────────────────────────

class CheckTodayAttendanceTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: CheckTodayAttendanceUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = CheckTodayAttendanceUseCase(repository)
    }

    @Test
    fun `오늘 이벤트가 없으면 미출석이다`() = runTest {
        every { repository.observeEventsForToday() } returns flowOf(emptyList())
        assertFalse(useCase().first())
    }

    @Test
    fun `오늘 PENDING 이벤트만 있으면 미출석이다`() = runTest {
        every { repository.observeEventsForToday() } returns flowOf(listOf(pendingEventOnDate(today())))
        assertFalse(useCase().first())
    }

    @Test
    fun `오늘 SAVED 이벤트가 하나라도 있으면 출석이다`() = runTest {
        every { repository.observeEventsForToday() } returns flowOf(listOf(savedEventOnDate(today())))
        assertTrue(useCase().first())
    }
}

// ──────────────────────────────────────────────
// GetWeeklyAttendance
// ──────────────────────────────────────────────

class GetWeeklyAttendanceTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: GetWeeklyAttendanceUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetWeeklyAttendanceUseCase(repository)
    }

    @Test
    fun `항상 7개의 항목을 반환한다`() = runTest {
        every { repository.observeAllEvents() } returns flowOf(emptyList())
        assertEquals(7, useCase().first().size)
    }

    @Test
    fun `dayIndex는 0부터 6까지 순서대로 채워진다`() = runTest {
        every { repository.observeAllEvents() } returns flowOf(emptyList())
        val indices = useCase().first().map { it.dayIndex }
        assertEquals((0..6).toList(), indices)
    }

    @Test
    fun `출석 데이터가 없으면 모든 요일이 미출석이다`() = runTest {
        every { repository.observeAllEvents() } returns flowOf(emptyList())
        assertTrue(useCase().first().none { it.isAttendance })
    }

    @Test
    fun `오늘에 SAVED 이벤트가 있으면 오늘 요일이 출석으로 표시된다`() = runTest {
        every { repository.observeAllEvents() } returns flowOf(listOf(savedEventOnDate(today())))

        val result = useCase().first()
        val todayIndex = LocalDate.now().dayOfWeek.value % 7
        val todayItem = result.find { it.dayIndex == todayIndex }

        assertTrue(todayItem?.isAttendance == true)
    }

    @Test
    fun `오늘 PENDING 이벤트만 있으면 오늘 요일은 미출석이다`() = runTest {
        every { repository.observeAllEvents() } returns flowOf(listOf(pendingEventOnDate(today())))

        val result = useCase().first()
        val todayIndex = LocalDate.now().dayOfWeek.value % 7
        val todayItem = result.find { it.dayIndex == todayIndex }

        assertFalse(todayItem?.isAttendance == true)
    }
}
