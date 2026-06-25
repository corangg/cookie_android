package com.nuecoo.feature.main.presentation.oven

import com.nuecoo.feature.main.domain.model.CookieItemData
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
import com.nuecoo.feature.main.domain.usecase.GetNewCookieNumberUseCase
import com.nuecoo.feature.main.domain.usecase.InitDailyCookieUseCase
import com.nuecoo.feature.main.domain.usecase.ObserveDailyCookieData
import com.nuecoo.feature.main.domain.usecase.ObserveNotOpenedCookies
import com.nuecoo.feature.main.domain.usecase.RemainTimeUseCase
import com.nuecoo.feature.main.domain.usecase.UpdateOpenCookieDataUseCase
import com.nuecoo.feature.main.presentation.oven.viewmodel.OvenViewModel
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────
// OvenViewModel
// 일일 쿠키 초기화·열기·선택 상태를 관리하는 ViewModel 테스트
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class OvenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var observeDailyCookieData: ObserveDailyCookieData
    private lateinit var remainTimeUseCase: RemainTimeUseCase
    private lateinit var observeNotOpenedCookies: ObserveNotOpenedCookies
    private lateinit var initDailyCookieUseCase: InitDailyCookieUseCase
    private lateinit var updateOpenCookieDataUseCase: UpdateOpenCookieDataUseCase
    private lateinit var getNewCookieNumberUseCase: GetNewCookieNumberUseCase
    private lateinit var viewModel: OvenViewModel

    // 테스트용 오늘 쿠키 데이터: Cheering 미열림, Comfort 열림
    private val fakeDailyCookieData = DailyCookieItemData(
        date = "20260626",
        list = listOf(
            CookieItemData(type = CookieType.Cheering.type, no = null),
            CookieItemData(type = CookieType.Comfort.type, no = 1),
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        observeDailyCookieData = mockk()
        remainTimeUseCase = mockk()
        observeNotOpenedCookies = mockk()
        initDailyCookieUseCase = mockk()
        updateOpenCookieDataUseCase = mockk()
        getNewCookieNumberUseCase = mockk()

        // ViewModel 생성 시 property initializer에서 호출되므로 사전에 반환값 지정
        every { observeDailyCookieData() } returns flowOf(fakeDailyCookieData)
        every { remainTimeUseCase() } returns flowOf("23 : 59 : 59")
        every { observeNotOpenedCookies() } returns flowOf(1)

        viewModel = OvenViewModel(
            observeDailyCookieData = observeDailyCookieData,
            remainTimeUseCase = remainTimeUseCase,
            observeNotOpenedCookies = observeNotOpenedCookies,
            initDailyCookieUseCase = initDailyCookieUseCase,
            updateOpenCookieDataUseCase = updateOpenCookieDataUseCase,
            getNewCookieNumberUseCase = getNewCookieNumberUseCase,
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
    fun `초기 selectedCookie는 null이다`() {
        // 쿠키를 선택하기 전 초기 상태
        assertNull(viewModel.selectedCookie.value)
    }

    @Test
    fun `remainTime의 구독 전 초기값은 00 00 00이다`() {
        // WhileSubscribed 구독 전에는 initialValue 반환
        assertEquals("00 : 00 : 00", viewModel.remainTime.value)
    }

    // ── WhileSubscribed 플로우 (backgroundScope + advanceUntilIdle 패턴) ──

    @Test
    fun `dailyCookieData가 UseCase의 값을 반영한다`() = runTest {
        // 오늘 쿠키 데이터가 상태에 올바르게 반영되는지 확인
        backgroundScope.launch { viewModel.dailyCookieData.collect {} }
        advanceUntilIdle()
        assertEquals(fakeDailyCookieData, viewModel.dailyCookieData.value)
    }

    @Test
    fun `notOpenedCookies가 미열림 쿠키 수를 반영한다`() = runTest {
        // 미열림 쿠키 개수가 상태에 올바르게 반영되는지 확인
        backgroundScope.launch { viewModel.notOpenedCookies.collect {} }
        advanceUntilIdle()
        assertEquals(1, viewModel.notOpenedCookies.value)
    }

    @Test
    fun `remainTime이 UseCase의 값을 반영한다`() = runTest {
        // 구독 후 남은 시간 문자열이 UseCase 반환값으로 업데이트됨
        backgroundScope.launch { viewModel.remainTime.collect {} }
        advanceUntilIdle()
        assertEquals("23 : 59 : 59", viewModel.remainTime.value)
    }

    // ── selectCookie / clearSelectedCookie ──

    @Test
    fun `selectCookie 호출 시 selectedCookie가 설정된다`() {
        // 쿠키를 클릭하면 selectedCookie에 해당 항목이 저장됨
        val cookie = CookieUIItemData(type = CookieType.Cheering.type, imgRes = 0)
        viewModel.selectCookie(cookie)
        assertEquals(cookie, viewModel.selectedCookie.value)
    }

    @Test
    fun `clearSelectedCookie 호출 시 selectedCookie가 null이 된다`() {
        // 쿠키 선택 해제 시 selectedCookie가 초기화됨
        val cookie = CookieUIItemData(type = CookieType.Cheering.type, imgRes = 0)
        viewModel.selectCookie(cookie)
        viewModel.clearSelectedCookie()
        assertNull(viewModel.selectedCookie.value)
    }

    // ── initDailyCookie ──

    @Test
    fun `initDailyCookie는 InitDailyCookieUseCase에 위임한다`() = runTest {
        // ViewModel이 직접 초기화 로직을 처리하지 않고 UseCase에 위임하는지 확인
        coEvery { initDailyCookieUseCase(any()) } just Runs

        viewModel.initDailyCookie(listOf(CookieType.Cheering to 5))

        coVerify(exactly = 1) { initDailyCookieUseCase(listOf(CookieType.Cheering to 5)) }
    }

    // ── updateOpenCookieData ──

    @Test
    fun `updateOpenCookieData 후 selectedCookie의 no와 isOpened가 갱신된다`() = runTest {
        // 쿠키를 열면 번호와 열림 상태가 selectedCookie에 반영되어야 함
        val cookie = CookieUIItemData(type = CookieType.Cheering.type, no = null, isOpened = false, imgRes = 0)
        viewModel.selectCookie(cookie)

        coEvery { getNewCookieNumberUseCase(CookieType.Cheering.type, 5) } returns 3
        coEvery { updateOpenCookieDataUseCase(type = CookieType.Cheering.type, newNo = 3) } just Runs

        viewModel.updateOpenCookieData(type = CookieType.Cheering.type, size = 5)

        assertEquals(3, viewModel.selectedCookie.value?.no)
        assertTrue(viewModel.selectedCookie.value?.isOpened == true)
    }

    @Test
    fun `selectedCookie가 null이면 updateOpenCookieData 후에도 null을 유지한다`() = runTest {
        // 선택된 쿠키가 없으면 열기 작업 후에도 selectedCookie가 null이어야 함
        coEvery { getNewCookieNumberUseCase(any(), any()) } returns 1
        coEvery { updateOpenCookieDataUseCase(type = any(), newNo = any()) } just Runs

        viewModel.updateOpenCookieData(type = 0, size = 5)

        assertNull(viewModel.selectedCookie.value)
    }

    @Test
    fun `updateOpenCookieData는 getNewCookieNumberUseCase의 반환값을 no로 사용한다`() = runTest {
        // 새 쿠키 번호는 UseCase가 결정한 값이어야 함
        val cookie = CookieUIItemData(type = CookieType.Comfort.type, no = null, isOpened = false, imgRes = 0)
        viewModel.selectCookie(cookie)

        coEvery { getNewCookieNumberUseCase(CookieType.Comfort.type, 10) } returns 7
        coEvery { updateOpenCookieDataUseCase(type = any(), newNo = any()) } just Runs

        viewModel.updateOpenCookieData(type = CookieType.Comfort.type, size = 10)

        assertEquals(7, viewModel.selectedCookie.value?.no)
    }
}
