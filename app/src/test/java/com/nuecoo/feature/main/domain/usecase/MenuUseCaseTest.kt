package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.main.domain.model.CookieItemData
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
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

// hasOpened=true 이면 no=1(열림), false 이면 no=null(미열림)인 하루치 데이터를 생성하는 헬퍼
private fun dailyData(date: String, hasOpened: Boolean = true) = DailyCookieItemData(
    date = date,
    list = listOf(CookieItemData(type = 0, no = if (hasOpened) 1 else null))
)

// ──────────────────────────────────────────────
// GetAttendanceCount
// 연속 출석 일수(스트릭)를 계산하는 UseCase 테스트
// ──────────────────────────────────────────────

class GetAttendanceCountTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: GetAttendanceCount

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetAttendanceCount(repository)
    }

    // 여러 날짜를 레포지토리에 주입하는 헬퍼
    private fun givenDates(vararg dates: String) {
        every { repository.getFlowCookieDataList() } returns flowOf(dates.map { dailyData(it) })
    }

    @Test
    fun `출석 기록이 없으면 스트릭은 0이다`() = runTest {
        // 아무 날짜도 없을 때 연속 출석 일수는 0
        givenDates()
        assertEquals(0, useCase().first())
    }

    @Test
    fun `오늘만 출석하면 스트릭은 1이다`() = runTest {
        // 오늘 하루만 기록된 경우 연속 일수는 1
        givenDates(today())
        assertEquals(1, useCase().first())
    }

    @Test
    fun `어제만 출석하면 스트릭은 1이다`() = runTest {
        // 오늘 기록은 없고 어제만 있어도 연속 1로 인정
        givenDates(daysAgo(1))
        assertEquals(1, useCase().first())
    }

    @Test
    fun `이틀 전만 출석하면 스트릭은 0이다`() = runTest {
        // 오늘·어제 모두 없고 이틀 전만 있으면 연속이 끊겼으므로 0
        givenDates(daysAgo(2))
        assertEquals(0, useCase().first())
    }

    @Test
    fun `오늘과 어제 모두 출석하면 스트릭은 2이다`() = runTest {
        // 연속 이틀 출석
        givenDates(today(), daysAgo(1))
        assertEquals(2, useCase().first())
    }

    @Test
    fun `오늘 포함 3일 연속 출석하면 스트릭은 3이다`() = runTest {
        // 오늘·어제·이틀 전 3일 연속
        givenDates(today(), daysAgo(1), daysAgo(2))
        assertEquals(3, useCase().first())
    }

    @Test
    fun `오늘은 있지만 어제가 없으면 스트릭은 1이다`() = runTest {
        // 오늘과 이틀 전이 있어도 어제가 빠지면 연속이 끊겨 오늘 하루만 카운트
        givenDates(today(), daysAgo(2))
        assertEquals(1, useCase().first())
    }

    @Test
    fun `어제와 이틀 전이 연속이면 스트릭은 2이다`() = runTest {
        // 오늘은 없지만 어제·이틀 전이 연속인 경우
        givenDates(daysAgo(1), daysAgo(2))
        assertEquals(2, useCase().first())
    }
}

// ──────────────────────────────────────────────
// CheckTodayAttendance
// 오늘 출석 여부를 확인하는 UseCase 테스트
// ──────────────────────────────────────────────

class CheckTodayAttendanceTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: CheckTodayAttendance

    @Before
    fun setUp() {
        repository = mockk()
        useCase = CheckTodayAttendance(repository)
    }

    @Test
    fun `오늘 데이터가 null이면 미출석으로 간주한다`() = runTest {
        // 저장된 오늘 데이터 자체가 없는 경우
        every { repository.getFlowDailyCookieData() } returns flowOf(null)
        assertFalse(useCase().first())
    }

    @Test
    fun `날짜가 오늘이 아닌 데이터는 미출석으로 간주한다`() = runTest {
        // 어제 날짜 데이터가 조회되더라도 오늘 출석으로 인정하지 않음
        every { repository.getFlowDailyCookieData() } returns flowOf(dailyData(daysAgo(1)))
        assertFalse(useCase().first())
    }

    @Test
    fun `오늘 데이터는 있지만 열린 쿠키가 없으면 미출석이다`() = runTest {
        // 오늘 날짜로 데이터가 생성됐지만 아직 쿠키를 하나도 열지 않은 상태
        val data = DailyCookieItemData(
            date = today(),
            list = listOf(CookieItemData(type = 0, no = null))
        )
        every { repository.getFlowDailyCookieData() } returns flowOf(data)
        assertFalse(useCase().first())
    }

    @Test
    fun `오늘 데이터에 열린 쿠키가 하나라도 있으면 출석이다`() = runTest {
        // no != null 인 쿠키가 하나 이상이면 출석으로 인정
        every { repository.getFlowDailyCookieData() } returns flowOf(dailyData(today(), hasOpened = true))
        assertTrue(useCase().first())
    }

    @Test
    fun `여러 쿠키 중 하나만 열려 있어도 출석이다`() = runTest {
        // 열리지 않은 쿠키와 열린 쿠키가 섞여 있을 때 열린 쿠키가 하나라도 있으면 출석
        val data = DailyCookieItemData(
            date = today(),
            list = listOf(
                CookieItemData(type = 0, no = null),
                CookieItemData(type = 1, no = 3),
            )
        )
        every { repository.getFlowDailyCookieData() } returns flowOf(data)
        assertTrue(useCase().first())
    }
}

// ──────────────────────────────────────────────
// GetWeeklyAttendance
// 이번 주(일~토) 7일치 출석 현황을 반환하는 UseCase 테스트
// ──────────────────────────────────────────────

class GetWeeklyAttendanceTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: GetWeeklyAttendance

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetWeeklyAttendance(repository)
    }

    @Test
    fun `항상 7개의 항목을 반환한다`() = runTest {
        // 데이터 유무와 관계없이 반환 리스트 크기는 항상 7(일~토)
        every { repository.getFlowCookieDataList() } returns flowOf(emptyList())
        assertEquals(7, useCase().first().size)
    }

    @Test
    fun `dayIndex는 0부터 6까지 순서대로 채워진다`() = runTest {
        // 일요일=0, 월요일=1 … 토요일=6 순서로 정렬되어야 함
        every { repository.getFlowCookieDataList() } returns flowOf(emptyList())
        val indices = useCase().first().map { it.dayIndex }
        assertEquals((0..6).toList(), indices)
    }

    @Test
    fun `출석 데이터가 없으면 모든 요일이 미출석이다`() = runTest {
        // 빈 리스트가 오면 7개 항목 모두 isAttendance=false
        every { repository.getFlowCookieDataList() } returns flowOf(emptyList())
        assertTrue(useCase().first().none { it.isAttendance })
    }

    @Test
    fun `오늘에 열린 쿠키가 있으면 오늘 요일이 출석으로 표시된다`() = runTest {
        // 오늘 날짜의 데이터에 열린 쿠키가 있을 때 해당 dayIndex가 출석 처리
        val todayData = listOf(dailyData(today(), hasOpened = true))
        every { repository.getFlowCookieDataList() } returns flowOf(todayData)

        val result = useCase().first()
        val todayIndex = LocalDate.now().dayOfWeek.value % 7
        val todayItem = result.find { it.dayIndex == todayIndex }

        assertTrue(todayItem?.isAttendance == true)
    }

    @Test
    fun `오늘 데이터가 있어도 열린 쿠키가 없으면 미출석이다`() = runTest {
        // 오늘 날짜의 데이터가 있더라도 쿠키를 하나도 열지 않았으면 미출석
        val todayData = listOf(dailyData(today(), hasOpened = false))
        every { repository.getFlowCookieDataList() } returns flowOf(todayData)

        val result = useCase().first()
        val todayIndex = LocalDate.now().dayOfWeek.value % 7
        val todayItem = result.find { it.dayIndex == todayIndex }

        assertFalse(todayItem?.isAttendance == true)
    }
}
