package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.core.util.getLocalTimeToString
import com.nuecoo.feature.main.domain.model.CookieItemData
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
import com.nuecoo.feature.main.domain.repository.CookieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// 테스트 전용 고정 시각 (getLocalTimeToString 모킹에 사용)
private const val FAKE_NOW = "20260626_143022"
private const val FAKE_TODAY = "20260626"

// ──────────────────────────────────────────────
// ObserveNotOpenedCookies
// 아직 열리지 않은 쿠키 개수를 관찰하는 UseCase 테스트
// ──────────────────────────────────────────────

class ObserveNotOpenedCookiesTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: ObserveNotOpenedCookies

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObserveNotOpenedCookies(repository)
    }

    @Test
    fun `오늘 데이터가 null이면 미열림 쿠키는 0이다`() = runTest {
        // 오늘 데이터 자체가 없을 때 0을 반환해야 함
        every { repository.getFlowDailyCookieData() } returns flowOf(null)
        assertEquals(0, useCase().first())
    }

    @Test
    fun `no가 null인 항목 수만큼 미열림 카운트를 반환한다`() = runTest {
        // no=null: 미열림 2개, no=1: 열림 1개 → 결과는 2
        val data = DailyCookieItemData(
            date = FAKE_TODAY,
            list = listOf(
                CookieItemData(type = 0, no = null),
                CookieItemData(type = 1, no = 1),
                CookieItemData(type = 2, no = null),
            )
        )
        every { repository.getFlowDailyCookieData() } returns flowOf(data)
        assertEquals(2, useCase().first())
    }

    @Test
    fun `모든 쿠키가 열려있으면 미열림 카운트는 0이다`() = runTest {
        // 전부 no 값이 있는 경우 미열림이 하나도 없음
        val data = DailyCookieItemData(
            date = FAKE_TODAY,
            list = listOf(
                CookieItemData(type = 0, no = 1),
                CookieItemData(type = 1, no = 2),
            )
        )
        every { repository.getFlowDailyCookieData() } returns flowOf(data)
        assertEquals(0, useCase().first())
    }

    @Test
    fun `모든 쿠키가 미열림이면 전체 개수를 반환한다`() = runTest {
        // 5개 모두 no=null인 경우 5를 반환
        val data = DailyCookieItemData(
            date = FAKE_TODAY,
            list = (0..4).map { CookieItemData(type = it, no = null) }
        )
        every { repository.getFlowDailyCookieData() } returns flowOf(data)
        assertEquals(5, useCase().first())
    }
}

// ──────────────────────────────────────────────
// InitDailyCookieUseCase
// 오늘 쿠키 데이터가 없을 때 초기 데이터를 생성하는 UseCase 테스트
// (getLocalTimeToString 는 현재 시각을 반환하므로 mockkStatic으로 고정)
// ──────────────────────────────────────────────

class InitDailyCookieUseCaseTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: InitDailyCookieUseCase

    @Before
    fun setUp() {
        // 현재 시각 함수를 고정값으로 대체
        mockkStatic("com.nuecoo.core.util.TimeUtilKt")
        every { getLocalTimeToString() } returns FAKE_NOW
        repository = mockk()
        useCase = InitDailyCookieUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `오늘 데이터가 이미 있으면 upsert를 호출하지 않는다`() = runTest {
        // 오늘 날짜로 데이터가 이미 저장되어 있으면 중복 생성하지 않아야 함
        val existingData = DailyCookieItemData(date = FAKE_TODAY, list = emptyList())
        coEvery { repository.getCookieDataList() } returns listOf(existingData)

        useCase(listOf(CookieType.Cheering to 10))

        coVerify(exactly = 0) { repository.upsertDailyCookieData(any()) }
    }

    @Test
    fun `오늘 데이터가 없으면 upsert를 한 번 호출한다`() = runTest {
        // 오늘 날짜 데이터가 없을 때 신규 생성 한 번 호출
        coEvery { repository.getCookieDataList() } returns emptyList()
        coEvery { repository.upsertDailyCookieData(any()) } returns true

        useCase(listOf(CookieType.Cheering to 10))

        coVerify(exactly = 1) { repository.upsertDailyCookieData(any()) }
    }

    @Test
    fun `새로 저장되는 데이터의 날짜는 오늘이다`() = runTest {
        // upsert 호출 시 date 필드가 FAKE_TODAY(오늘)로 설정되어야 함
        val captured = slot<DailyCookieItemData>()
        coEvery { repository.getCookieDataList() } returns emptyList()
        coEvery { repository.upsertDailyCookieData(capture(captured)) } returns true

        useCase(listOf(CookieType.Cheering to 10))

        assertEquals(FAKE_TODAY, captured.captured.date)
    }

    @Test
    fun `생성된 리스트는 Unknown을 제외한 모든 CookieType 수만큼 항목을 가진다`() = runTest {
        // Unknown 을 제외한 CookieType 각각에 대한 초기 항목이 만들어져야 함
        val captured = slot<DailyCookieItemData>()
        coEvery { repository.getCookieDataList() } returns emptyList()
        coEvery { repository.upsertDailyCookieData(capture(captured)) } returns true

        useCase(listOf(CookieType.Cheering to 10))

        val validTypeCount = CookieType.entries.count { it != CookieType.Unknown }
        assertEquals(validTypeCount, captured.captured.list.size)
    }

    @Test
    fun `해당 타입을 이미 가득 모았으면 isFull이 true이다`() = runTest {
        // Cheering 총 1개인데 과거에 이미 1개 수집했으면 오늘 항목의 isFull=true
        val captured = slot<DailyCookieItemData>()
        val pastData = DailyCookieItemData(
            date = "20260620",
            list = listOf(CookieItemData(type = CookieType.Cheering.type, no = 1))
        )
        coEvery { repository.getCookieDataList() } returns listOf(pastData)
        coEvery { repository.upsertDailyCookieData(capture(captured)) } returns true

        useCase(listOf(CookieType.Cheering to 1, CookieType.Comfort to 5))

        val cheeringItem = captured.captured.list.find { it.type == CookieType.Cheering.type }
        assertTrue(cheeringItem?.isFull == true)
    }
}

// ──────────────────────────────────────────────
// UpdateOpenCookieDataUseCase
// 쿠키를 열었을 때 오늘 데이터를 갱신하는 UseCase 테스트
// ──────────────────────────────────────────────

class UpdateOpenCookieDataUseCaseTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: UpdateOpenCookieDataUseCase

    @Before
    fun setUp() {
        mockkStatic("com.nuecoo.core.util.TimeUtilKt")
        every { getLocalTimeToString() } returns FAKE_NOW
        repository = mockk()
        useCase = UpdateOpenCookieDataUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `오늘 데이터가 없으면 upsert를 호출하지 않는다`() = runTest {
        // 오늘치 데이터가 없는 상태에서 업데이트 시도하면 아무 것도 하지 않음
        coEvery { repository.getCookieDataList() } returns emptyList()

        useCase(type = 0, newNo = 5)

        coVerify(exactly = 0) { repository.upsertDailyCookieData(any()) }
    }

    @Test
    fun `전달한 타입의 쿠키에 새 번호와 isOpened=true가 저장된다`() = runTest {
        // type=0 쿠키를 newNo=3 으로 열었을 때 해당 항목만 갱신
        val todayData = DailyCookieItemData(
            date = FAKE_TODAY,
            list = listOf(
                CookieItemData(type = 0, no = null, isOpened = false),
                CookieItemData(type = 1, no = null, isOpened = false),
            )
        )
        val captured = slot<DailyCookieItemData>()
        coEvery { repository.getCookieDataList() } returns listOf(todayData)
        coEvery { repository.upsertDailyCookieData(capture(captured)) } returns true

        useCase(type = 0, newNo = 3)

        val updated = captured.captured.list.find { it.type == 0 }!!
        assertEquals(3, updated.no)       // 새 번호가 반영됨
        assertTrue(updated.isOpened!!)    // 열린 상태로 변경됨
    }

    @Test
    fun `다른 타입의 쿠키는 변경되지 않는다`() = runTest {
        // type=0 을 열었을 때 type=1 쿠키는 그대로 유지되어야 함
        val todayData = DailyCookieItemData(
            date = FAKE_TODAY,
            list = listOf(
                CookieItemData(type = 0, no = null, isOpened = false),
                CookieItemData(type = 1, no = null, isOpened = false),
            )
        )
        val captured = slot<DailyCookieItemData>()
        coEvery { repository.getCookieDataList() } returns listOf(todayData)
        coEvery { repository.upsertDailyCookieData(capture(captured)) } returns true

        useCase(type = 0, newNo = 3)

        val untouched = captured.captured.list.find { it.type == 1 }!!
        assertNull(untouched.no)          // 번호 변화 없음
        assertFalse(untouched.isOpened!!) // 닫힌 상태 유지
    }
}
