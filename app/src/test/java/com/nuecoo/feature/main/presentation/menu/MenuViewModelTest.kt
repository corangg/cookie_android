package com.nuecoo.feature.main.presentation.menu

import com.nuecoo.core.domain.model.UserInfo
import com.nuecoo.core.domain.usecase.GetWidgetEnabledUseCase
import com.nuecoo.core.domain.usecase.SaveWidgetEnabledUseCase
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.TypeCollectedCount
import com.nuecoo.feature.main.domain.model.WeeklyAttendanceModel
import com.nuecoo.feature.main.domain.usecase.CheckTodayAttendanceUseCase
import com.nuecoo.feature.main.domain.usecase.GetAttendanceCount
import com.nuecoo.feature.main.domain.usecase.GetAttendanceDatesUseCase
import com.nuecoo.feature.main.domain.usecase.GetCollectionByTypeUseCase
import com.nuecoo.feature.main.domain.usecase.GetWeeklyAttendanceUseCase
import com.nuecoo.feature.main.domain.usecase.LogOutUseCase
import com.nuecoo.feature.main.domain.usecase.ObserveCollectionProgressUseCase
import com.nuecoo.feature.main.domain.usecase.ObserveUserInfoUseCase
import com.nuecoo.feature.main.domain.usecase.RefreshUserInfoUseCase
import com.nuecoo.feature.main.presentation.menu.viewmodel.MenuViewModel
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
//
// [아키텍처 변경 메모]
// 로그아웃 후 화면 이동은 더 이상 MenuViewModel._isLoggedIn 으로 처리하지 않는다.
// Firebase 인증 상태를 MainViewModel 이 ObserveAuthStateUseCase 로 직접 관찰하며,
// AppNavigation 의 LaunchedEffect 가 isLoggedIn 값에 따라 전체 스택을 교체한다.
//
// collectionProgress는 더 이상 loadCollectionProgress(List<Pair<CookieType, Int>>)를
// 직접 호출해 계산하지 않는다. ObserveCollectionProgressUseCase가 CookieRepository의
// 진행도를 그대로 관찰하며, ViewModel 생성 시점에 Flow가 한 번 캡처되므로 값을 바꾸려면
// UseCase의 반환값을 다시 스텁한 뒤 ViewModel을 재생성해야 한다.
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getCollectionByTypeUseCase: GetCollectionByTypeUseCase
    private lateinit var logoutUseCase: LogOutUseCase
    private lateinit var saveWidgetEnabledUseCase: SaveWidgetEnabledUseCase
    private lateinit var refreshUserInfoUseCase: RefreshUserInfoUseCase
    private lateinit var observeUserInfoUseCase: ObserveUserInfoUseCase
    private lateinit var getAttendanceCount: GetAttendanceCount
    private lateinit var checkTodayAttendanceUseCase: CheckTodayAttendanceUseCase
    private lateinit var getWeeklyAttendanceUseCase: GetWeeklyAttendanceUseCase
    private lateinit var getAttendanceDatesUseCase: GetAttendanceDatesUseCase
    private lateinit var getWidgetEnabledUseCase: GetWidgetEnabledUseCase
    private lateinit var observeCollectionProgressUseCase: ObserveCollectionProgressUseCase
    private lateinit var viewModel: MenuViewModel

    // 테스트용 주간 출석 데이터 (7일치)
    private val fakeWeekly =
        (0..6).map { WeeklyAttendanceModel(dayIndex = it, isAttendance = it % 2 == 0) }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getCollectionByTypeUseCase = mockk()
        logoutUseCase = mockk()
        saveWidgetEnabledUseCase = mockk()
        refreshUserInfoUseCase = mockk()
        observeUserInfoUseCase = mockk()
        getAttendanceCount = mockk()
        checkTodayAttendanceUseCase = mockk()
        getWeeklyAttendanceUseCase = mockk()
        getAttendanceDatesUseCase = mockk()
        getWidgetEnabledUseCase = mockk()
        observeCollectionProgressUseCase = mockk()

        // ViewModel 생성 시 property initializer에서 호출되므로 사전에 반환값 지정
        every { observeUserInfoUseCase() } returns flowOf(null)
        every { getAttendanceCount() } returns flowOf(5)
        every { checkTodayAttendanceUseCase() } returns flowOf(true)
        every { getWeeklyAttendanceUseCase() } returns flowOf(fakeWeekly)
        every { getAttendanceDatesUseCase() } returns flowOf(emptySet())
        every { getWidgetEnabledUseCase() } returns flowOf(false)
        every { observeCollectionProgressUseCase() } returns flowOf(emptyList())

        createViewModel()
    }

    private fun createViewModel() {
        viewModel = MenuViewModel(
            getCollectionByTypeUseCase = getCollectionByTypeUseCase,
            logoutUseCase = logoutUseCase,
            saveWidgetEnabledUseCase = saveWidgetEnabledUseCase,
            refreshUserInfoUseCase = refreshUserInfoUseCase,
            observeUserInfoUseCase = observeUserInfoUseCase,
            getAttendanceCount = getAttendanceCount,
            checkTodayAttendanceUseCase = checkTodayAttendanceUseCase,
            getWeeklyAttendanceUseCase = getWeeklyAttendanceUseCase,
            getAttendanceDatesUseCase = getAttendanceDatesUseCase,
            getWidgetEnabledUseCase = getWidgetEnabledUseCase,
            observeCollectionProgressUseCase = observeCollectionProgressUseCase,
            mainDispatcher = Dispatchers.Main as MainCoroutineDispatcher,
            defaultDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── 초기 상태 (WhileSubscribed 정책이므로 구독 전에는 initialValue를 유지) ──

    @Test
    fun `초기 collectionProgress는 비어있다`() {
        assertTrue(viewModel.collectionProgress.value.isEmpty())
    }

    @Test
    fun `초기 isLoggedIn은 true이다`() {
        // 메인 화면에 진입한 시점은 이미 로그인된 상태이므로 초기값은 true
        assertTrue(viewModel.isLoggedIn.value)
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

    @Test
    fun `nickname은 observeUserInfoUseCase가 방출한 UserInfo의 닉네임을 반영한다`() = runTest {
        // observeUserInfoUseCase()는 생성 시점에 캡처되므로 재생성 후 검증
        every { observeUserInfoUseCase() } returns flowOf(
            UserInfo(email = "a@test.com", nickname = "쿠키러버", phone = "", birth = "", gender = true)
        )
        createViewModel()

        backgroundScope.launch { viewModel.nickname.collect {} }
        advanceUntilIdle()
        assertEquals("쿠키러버", viewModel.nickname.value)
    }

    // ── collectionProgress ──

    @Test
    fun `collectionProgress는 ObserveCollectionProgressUseCase가 방출한 값을 반영한다`() = runTest {
        val progress = listOf(
            TypeCollectedCount(type = CookieType.Cheering.type, collectedCount = 2, maxCount = 3),
            TypeCollectedCount(type = CookieType.Comfort.type, collectedCount = 1, maxCount = 2),
        )
        every { observeCollectionProgressUseCase() } returns flowOf(progress)
        createViewModel()

        backgroundScope.launch { viewModel.collectionProgress.collect {} }
        advanceUntilIdle()

        assertEquals(progress, viewModel.collectionProgress.value)
    }

    // ── saveWidgetEnabled ──

    @Test
    fun `saveWidgetEnabled는 UseCase에 값을 전달한다`() = runTest {
        // 위젯 활성화 설정을 저장하면 UseCase에 정확히 위임
        coEvery { saveWidgetEnabledUseCase(any()) } just Runs

        viewModel.saveWidgetEnabled(true)

        coVerify(exactly = 1) { saveWidgetEnabledUseCase(true) }
    }

    // ── refreshUserInfo ──

    @Test
    fun `refreshUserInfo는 RefreshUserInfoUseCase에 위임한다`() = runTest {
        coEvery { refreshUserInfoUseCase() } just Runs

        viewModel.refreshUserInfo()

        coVerify(exactly = 1) { refreshUserInfoUseCase() }
    }

    // ── logout ──
    // [주의] AuthRepositoryImpl.logOut() 반환값이 변경됨:
    //   true  = Firebase signOut 성공 (로그아웃 완료)
    //   false = Firebase signOut 실패 (예외 발생)
    //
    // 실제 화면 전환은 MainViewModel 이 ObserveAuthStateUseCase 로 Firebase 상태를
    // 실시간 감지하여 처리하므로 MenuViewModel._isLoggedIn 은 더 이상 네비게이션에
    // 사용되지 않는다. logout() 호출이 LogOutUseCase 에 올바르게 위임되는지만 검증한다.

    @Test
    fun `logout은 LogOutUseCase에 위임한다`() = runTest {
        // ViewModel 이 직접 로그아웃 로직을 처리하지 않고 UseCase 에 위임하는지 확인
        coEvery { logoutUseCase() } returns true

        viewModel.logout()

        coVerify(exactly = 1) { logoutUseCase() }
    }

    @Test
    fun `logout 호출 시 UseCase 반환값이 isLoggedIn에 반영된다`() = runTest {
        // logoutUseCase() 반환값이 _isLoggedIn 에 그대로 저장됨
        // true = signOut 성공, false = signOut 실패 (현재 _isLoggedIn 은 네비게이션 미사용)
        coEvery { logoutUseCase() } returns true
        viewModel.logout()
        assertTrue(viewModel.isLoggedIn.value)

        coEvery { logoutUseCase() } returns false
        viewModel.logout()
        assertFalse(viewModel.isLoggedIn.value)
    }
}
