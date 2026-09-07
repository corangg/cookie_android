package com.nuecoo.feature.main.presentation.oven

import com.nuecoo.core.domain.model.CookieEvent
import com.nuecoo.core.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.domain.model.CookieSlotUi
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.usecase.GetCookieTotalCountsUseCase
import com.nuecoo.feature.main.domain.usecase.ObserveCollectionProgressUseCase
import com.nuecoo.feature.main.domain.usecase.ObserveDailyCookieData
import com.nuecoo.feature.main.domain.usecase.ObserveNotOpenedCookies
import com.nuecoo.feature.main.domain.usecase.OpenCookieUseCase
import com.nuecoo.feature.main.domain.usecase.RemainTimeUseCase
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
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────
// OvenViewModel
// 일일 쿠키 슬롯·열기·선택 상태를 관리하는 ViewModel 테스트
//
// [아키텍처 변경 메모]
// CookieRepository를 직접 주입받지 않는다. 쿠키 열기는 OpenCookieUseCase에,
// 진행도는 ObserveCollectionProgressUseCase에 위임한다.
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class OvenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var openCookieUseCase: OpenCookieUseCase
    private lateinit var getCookieTotalCountsUseCase: GetCookieTotalCountsUseCase
    private lateinit var observeCollectionProgressUseCase: ObserveCollectionProgressUseCase
    private lateinit var observeDailyCookieData: ObserveDailyCookieData
    private lateinit var remainTimeUseCase: RemainTimeUseCase
    private lateinit var observeNotOpenedCookies: ObserveNotOpenedCookies
    private lateinit var viewModel: OvenViewModel

    private val fakeSlots = listOf(
        CookieSlotUi.Empty(CookieType.Cheering.type),
        CookieSlotUi.Filled(
            type = CookieType.Comfort.type,
            events = listOf(
                CookieEvent(
                    eventId = "id1",
                    datetime = "202606261200",
                    claimDate = "20260626",
                    message = null,
                    type = CookieType.Comfort.type,
                    cookieNo = 1,
                    syncStatus = CookieSyncStatus.SAVED
                )
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        openCookieUseCase = mockk()
        getCookieTotalCountsUseCase = mockk()
        observeCollectionProgressUseCase = mockk()
        observeDailyCookieData = mockk()
        remainTimeUseCase = mockk()
        observeNotOpenedCookies = mockk()

        every { observeCollectionProgressUseCase() } returns flowOf(emptyList())
        every { observeDailyCookieData() } returns flowOf(fakeSlots)
        every { remainTimeUseCase() } returns flowOf("23 : 59 : 59")
        every { observeNotOpenedCookies() } returns flowOf(1)

        viewModel = OvenViewModel(
            openCookieUseCase = openCookieUseCase,
            getCookieTotalCountsUseCase = getCookieTotalCountsUseCase,
            observeCollectionProgressUseCase = observeCollectionProgressUseCase,
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
    fun `selectCookie 호출 시 해당 타입의 슬롯이 selectedCookie에 반영된다`() = runTest {
        backgroundScope.launch { viewModel.selectedCookie.collect {} }
        advanceUntilIdle()
        viewModel.selectCookie(CookieType.Cheering.type)
        assertEquals(CookieType.Cheering.type, viewModel.selectedCookie.value?.type)
    }

    @Test
    fun `openCookie는 OpenCookieUseCase에 위임한다`() = runTest {
        coEvery { openCookieUseCase(any()) } just Runs
        viewModel.openCookie(CookieType.Cheering.type)
        advanceUntilIdle()
        coVerify(exactly = 1) { openCookieUseCase(CookieType.Cheering.type) }
    }
}
