package com.nuecoo.feature.main.presentation.oven

import com.nuecoo.feature.main.domain.model.CookieEvent
import com.nuecoo.feature.main.domain.model.CookieSlotUi
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.repository.CookieRepository
import com.nuecoo.feature.main.domain.usecase.ObserveDailyCookieData
import com.nuecoo.feature.main.domain.usecase.ObserveNotOpenedCookies
import com.nuecoo.feature.main.domain.usecase.RemainTimeUseCase
import com.nuecoo.feature.main.presentation.oven.viewmodel.OvenViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
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
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────
// OvenViewModel
// 일일 쿠키 슬롯·열기·선택 상태를 관리하는 ViewModel 테스트
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class OvenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var cookieRepository: CookieRepository
    private lateinit var observeDailyCookieData: ObserveDailyCookieData
    private lateinit var remainTimeUseCase: RemainTimeUseCase
    private lateinit var observeNotOpenedCookies: ObserveNotOpenedCookies
    private lateinit var viewModel: OvenViewModel

    private val fakeSlots = listOf(
        CookieSlotUi.Empty(CookieType.Cheering.type),
        CookieSlotUi.Filled(
            type = CookieType.Comfort.type,
            events = listOf(
                CookieEvent("id1", "202606261200", "20260626", CookieType.Comfort.type, 1, CookieSyncStatus.SAVED)
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cookieRepository = mockk()
        observeDailyCookieData = mockk()
        remainTimeUseCase = mockk()
        observeNotOpenedCookies = mockk()

        every { observeDailyCookieData() } returns flowOf(fakeSlots)
        every { remainTimeUseCase() } returns flowOf("23 : 59 : 59")
        every { observeNotOpenedCookies() } returns flowOf(1)

        viewModel = OvenViewModel(
            cookieRepository = cookieRepository,
            observeDailyCookieData = observeDailyCookieData,
            remainTimeUseCase = remainTimeUseCase,
            observeNotOpenedCookies = observeNotOpenedCookies,
            mainDispatcher = Dispatchers.Main as MainCoroutineDispatcher,
            defaultDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 selectedCookie는 null이다`() {
        assertNull(viewModel.selectedCookie.value)
    }

    @Test
    fun `remainTime의 구독 전 초기값은 00 00 00이다`() {
        assertEquals("00 : 00 : 00", viewModel.remainTime.value)
    }

    @Test
    fun `dailyCookieSlots가 UseCase의 값을 반영한다`() = runTest {
        backgroundScope.launch { viewModel.dailyCookieSlots.collect {} }
        advanceUntilIdle()
        assertEquals(fakeSlots, viewModel.dailyCookieSlots.value)
    }

    @Test
    fun `notOpenedCookies가 미열림 쿠키 수를 반영한다`() = runTest {
        backgroundScope.launch { viewModel.notOpenedCookies.collect {} }
        advanceUntilIdle()
        assertEquals(1, viewModel.notOpenedCookies.value)
    }

    @Test
    fun `remainTime이 UseCase의 값을 반영한다`() = runTest {
        backgroundScope.launch { viewModel.remainTime.collect {} }
        advanceUntilIdle()
        assertEquals("23 : 59 : 59", viewModel.remainTime.value)
    }

    @Test
    fun `selectCookie 후 clearSelectedCookie 호출 시 selectedCookie가 null이 된다`() = runTest {
        backgroundScope.launch { viewModel.selectedCookie.collect {} }
        advanceUntilIdle()
        viewModel.selectCookie(CookieType.Cheering.type)
        viewModel.clearSelectedCookie()
        assertNull(viewModel.selectedCookie.value)
    }

    @Test
    fun `openCookie는 repository의 openCookie에 위임한다`() = runTest {
        coEvery { cookieRepository.openCookie(any()) } just Runs
        viewModel.openCookie(CookieType.Cheering.type)
        advanceUntilIdle()
        coVerify(exactly = 1) { cookieRepository.openCookie(CookieType.Cheering.type) }
    }
}
