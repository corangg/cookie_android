package com.nuecoo.feature.main.presentation.menu

import com.nuecoo.feature.auth.domain.usecase.LogoutUseCase
import com.nuecoo.feature.main.domain.model.CollectionDisplayItem
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.WeeklyAttendanceModel
import com.nuecoo.feature.main.domain.usecase.CheckTodayAttendance
import com.nuecoo.feature.main.domain.usecase.GetAttendanceCount
import com.nuecoo.feature.main.domain.usecase.GetCollectionByTypeUseCase
import com.nuecoo.feature.main.domain.usecase.GetWeeklyAttendance
import com.nuecoo.feature.main.presentation.menu.viewmodel.MenuViewModel
import com.nuecoo.feature.widget.domain.usecase.GetWidgetEnabledUseCase
import com.nuecoo.feature.widget.domain.usecase.SaveWidgetEnabledUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────
// MenuViewModel
// 출석·컬렉션 진행도·위젯 설정·로그아웃을 관리하는 ViewModel 테스트
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getCollectionByTypeUseCase: GetCollectionByTypeUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var saveWidgetEnabledUseCase: SaveWidgetEnabledUseCase
    private lateinit var getAttendanceCount: GetAttendanceCount
    private lateinit var checkTodayAttendance: CheckTodayAttendance
    private lateinit var getWeeklyAttendance: GetWeeklyAttendance
    private lateinit var getWidgetEnabledUseCase: GetWidgetEnabledUseCase
    private lateinit var viewModel: MenuViewModel

    // 테스트용 주간 출석 데이터 (7일치)
    private val fakeWeekly = (0..6).map { WeeklyAttendanceModel(dayIndex = it, isAttendance = it % 2 == 0) }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getCollectionByTypeUseCase = mockk()
        logoutUseCase = mockk()
        saveWidgetEnabledUseCase = mockk()
        getAttendanceCount = mockk()
        checkTodayAttendance = mockk()
        getWeeklyAttendance = mockk()
        getWidgetEnabledUseCase = mockk()

        // ViewModel 생성 시 property initializer에서 호출되므로 사전에 반환값 지정
        every { getAttendanceCount() } returns flowOf(5)
        every { checkTodayAttendance() } returns flowOf(true)
        every { getWeeklyAttendance() } returns flowOf(fakeWeekly)
        every { getWidgetEnabledUseCase() } returns flowOf(false)

        viewModel = MenuViewModel(
            getCollectionByTypeUseCase = getCollectionByTypeUseCase,
            logoutUseCase = logoutUseCase,
            saveWidgetEnabledUseCase = saveWidgetEnabledUseCase,
            getAttendanceCount = getAttendanceCount,
            checkTodayAttendance = checkTodayAttendance,
            getWeeklyAttendance = getWeeklyAttendance,
            getWidgetEnabledUseCase = getWidgetEnabledUseCase,
            mainDispatcher = Dispatchers.Main as MainCoroutineDispatcher,
            defaultDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── 초기 상태 ──

    @Test
    fun `초기 collectionProgress는 비어있다`() {
        // loadCollectionProgress 호출 전에는 진행도 목록이 없어야 함
        assertTrue(viewModel.collectionProgress.value.isEmpty())
    }

    // ── WhileSubscribed 플로우 (backgroundScope + advanceUntilIdle 패턴) ──

    @Test
    fun `attendanceCount가 UseCase의 값을 반영한다`() = runTest {
        // 연속 출석 횟수가 올바르게 상태에 반영되는지 확인
        backgroundScope.launch { viewModel.attendanceCount.collect {} }
        advanceUntilIdle()
        assertEquals(5, viewModel.attendanceCount.value)
    }

    @Test
    fun `isTodayAttendance가 UseCase의 값을 반영한다`() = runTest {
        // 오늘 출석 여부가 상태에 올바르게 반영되는지 확인
        backgroundScope.launch { viewModel.isTodayAttendance.collect {} }
        advanceUntilIdle()
        assertTrue(viewModel.isTodayAttendance.value)
    }

    @Test
    fun `weeklyAttendance가 7개 항목을 반환한다`() = runTest {
        // 일주일(7일) 출석 현황 데이터가 올바르게 채워지는지 확인
        backgroundScope.launch { viewModel.weeklyAttendance.collect {} }
        advanceUntilIdle()
        assertEquals(7, viewModel.weeklyAttendance.value.size)
    }

    @Test
    fun `weeklyAttendance의 dayIndex가 0부터 6까지 순서대로 배치된다`() = runTest {
        // 일요일(0)부터 토요일(6)까지 순서 보장
        backgroundScope.launch { viewModel.weeklyAttendance.collect {} }
        advanceUntilIdle()
        val indices = viewModel.weeklyAttendance.value.map { it.dayIndex }
        assertEquals((0..6).toList(), indices)
    }

    @Test
    fun `widgetEnabled의 초기값은 false이다`() = runTest {
        // 위젯 활성화 설정의 초기 상태 확인
        backgroundScope.launch { viewModel.widgetEnabled.collect {} }
        advanceUntilIdle()
        assertFalse(viewModel.widgetEnabled.value)
    }

    // ── loadCollectionProgress ──

    @Test
    fun `loadCollectionProgress는 수집 수량을 올바르게 계산한다`() = runTest {
        // Cheering 3개 중 2개 수집 → collected=2, total=3
        coEvery { getCollectionByTypeUseCase(CookieType.Cheering.type, 3) } returns listOf(
            CollectionDisplayItem(no = 1, type = CookieType.Cheering.type, isCollected = true, date = "20260620"),
            CollectionDisplayItem(no = 2, type = CookieType.Cheering.type, isCollected = false, date = null),
            CollectionDisplayItem(no = 3, type = CookieType.Cheering.type, isCollected = true, date = "20260622"),
        )

        viewModel.loadCollectionProgress(listOf(CookieType.Cheering to 3))

        val progress = viewModel.collectionProgress.value
        assertEquals(1, progress.size)
        assertEquals(CookieType.Cheering.type, progress[0].type)
        assertEquals(2, progress[0].collected)
        assertEquals(3, progress[0].total)
    }

    @Test
    fun `loadCollectionProgress는 여러 타입을 동시에 처리한다`() = runTest {
        // Cheering 1개, Comfort 2개 수집 → 각각 정확히 계산
        coEvery { getCollectionByTypeUseCase(CookieType.Cheering.type, 3) } returns listOf(
            CollectionDisplayItem(no = 1, type = CookieType.Cheering.type, isCollected = true, date = "20260620"),
            CollectionDisplayItem(no = 2, type = CookieType.Cheering.type, isCollected = false, date = null),
            CollectionDisplayItem(no = 3, type = CookieType.Cheering.type, isCollected = false, date = null),
        )
        coEvery { getCollectionByTypeUseCase(CookieType.Comfort.type, 2) } returns listOf(
            CollectionDisplayItem(no = 1, type = CookieType.Comfort.type, isCollected = true, date = "20260621"),
            CollectionDisplayItem(no = 2, type = CookieType.Comfort.type, isCollected = true, date = "20260622"),
        )

        viewModel.loadCollectionProgress(listOf(CookieType.Cheering to 3, CookieType.Comfort to 2))

        val progress = viewModel.collectionProgress.value
        assertEquals(2, progress.size)
        assertEquals(1, progress.find { it.type == CookieType.Cheering.type }?.collected)
        assertEquals(2, progress.find { it.type == CookieType.Comfort.type }?.collected)
    }

    @Test
    fun `아무것도 수집하지 않으면 collected가 0이다`() = runTest {
        // 전부 미수집 상태이면 collected=0
        coEvery { getCollectionByTypeUseCase(any(), any()) } returns listOf(
            CollectionDisplayItem(no = 1, type = CookieType.Cheering.type, isCollected = false, date = null),
        )

        viewModel.loadCollectionProgress(listOf(CookieType.Cheering to 1))

        assertEquals(0, viewModel.collectionProgress.value[0].collected)
    }

    // ── saveWidgetEnabled ──

    @Test
    fun `saveWidgetEnabled는 UseCase에 값을 전달한다`() = runTest {
        // 위젯 활성화 설정을 저장하면 UseCase에 정확히 위임
        coEvery { saveWidgetEnabledUseCase(any()) } just Runs

        viewModel.saveWidgetEnabled(true)

        coVerify(exactly = 1) { saveWidgetEnabledUseCase(true) }
    }

    // ── logout ──

    @Test
    fun `logout은 LogoutUseCase에 위임하고 결과를 반환한다`() = runTest {
        // 로그아웃 요청이 UseCase로 위임되고 결과가 그대로 반환되는지 확인
        coEvery { logoutUseCase() } returns true

        val result = viewModel.logout()

        assertTrue(result)
        coVerify(exactly = 1) { logoutUseCase() }
    }
}
