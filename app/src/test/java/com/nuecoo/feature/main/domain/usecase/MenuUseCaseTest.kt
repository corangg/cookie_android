package com.nuecoo.feature.main.domain.usecase

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

// ──────────────────────────────────────────────
// GetAttendanceCount
//
// [아키텍처 변경 메모]
// 출석 스트릭은 더 이상 CookieEvent 목록(observeAllEvents)에서 계산하지 않는다.
// CookieRepository.observeDailyClaimDates()가 이미 distinct 한 날짜 문자열
// (yyyyMMdd) 목록을 반환하므로 이를 직접 사용한다.
// ──────────────────────────────────────────────

class GetAttendanceCountTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: GetAttendanceCount

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetAttendanceCount(repository)
    }

    private fun givenClaimDates(vararg dates: String) {
        every { repository.observeDailyClaimDates() } returns flowOf(dates.toList())
    }

    @Test
    fun `출석 기록이 없으면 스트릭은 0이다`() = runTest {
        givenClaimDates()
        assertEquals(0, useCase().first())
    }

    @Test
    fun `오늘만 출석하면 스트릭은 1이다`() = runTest {
        givenClaimDates(today())
        assertEquals(1, useCase().first())
    }

    @Test
    fun `어제만 출석하면 스트릭은 1이다`() = runTest {
        givenClaimDates(daysAgo(1))
        assertEquals(1, useCase().first())
    }

    @Test
    fun `이틀 전만 출석하면 스트릭은 0이다`() = runTest {
        givenClaimDates(daysAgo(2))
        assertEquals(0, useCase().first())
    }

    @Test
    fun `오늘과 어제 모두 출석하면 스트릭은 2이다`() = runTest {
        givenClaimDates(today(), daysAgo(1))
        assertEquals(2, useCase().first())
    }

    @Test
    fun `오늘 포함 3일 연속 출석하면 스트릭은 3이다`() = runTest {
        givenClaimDates(today(), daysAgo(1), daysAgo(2))
        assertEquals(3, useCase().first())
    }

    @Test
    fun `오늘은 있지만 어제가 없으면 스트릭은 1이다`() = runTest {
        givenClaimDates(today(), daysAgo(2))
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
    fun `오늘 날짜가 출석 목록에 없으면 미출석이다`() = runTest {
        every { repository.observeDailyClaimDates() } returns flowOf(emptyList())
        assertFalse(useCase().first())
    }

    @Test
    fun `과거 날짜만 출석 목록에 있으면 미출석이다`() = runTest {
        every { repository.observeDailyClaimDates() } returns flowOf(listOf(daysAgo(1)))
        assertFalse(useCase().first())
    }

    @Test
    fun `오늘 날짜가 출석 목록에 있으면 출석이다`() = runTest {
        every { repository.observeDailyClaimDates() } returns flowOf(listOf(today()))
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
        every { repository.observeDailyClaimDates() } returns flowOf(emptyList())
        assertEquals(7, useCase().first().size)
    }

    @Test
    fun `dayIndex는 0부터 6까지 순서대로 채워진다`() = runTest {
        every { repository.observeDailyClaimDates() } returns flowOf(emptyList())
        val indices = useCase().first().map { it.dayIndex }
        assertEquals((0..6).toList(), indices)
    }

    @Test
    fun `출석 데이터가 없으면 모든 요일이 미출석이다`() = runTest {
        every { repository.observeDailyClaimDates() } returns flowOf(emptyList())
        assertTrue(useCase().first().none { it.isAttendance })
    }

    @Test
    fun `오늘 날짜가 출석 목록에 있으면 오늘 요일이 출석으로 표시된다`() = runTest {
        every { repository.observeDailyClaimDates() } returns flowOf(listOf(today()))

        val result = useCase().first()
        val todayIndex = LocalDate.now().dayOfWeek.value % 7
        val todayItem = result.find { it.dayIndex == todayIndex }

        assertTrue(todayItem?.isAttendance == true)
    }
}
