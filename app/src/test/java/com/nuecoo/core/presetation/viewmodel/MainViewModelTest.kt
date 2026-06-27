package com.nuecoo.core.presetation.viewmodel

import com.nuecoo.feature.auth.domain.usecase.ObserveAuthStateUseCase
import io.mockk.every
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────
// MainViewModel
// Firebase 인증 상태를 실시간으로 관찰하여 AppNavigation 에 전달하는 ViewModel 테스트
//
// [역할]
// ObserveAuthStateUseCase 가 방출하는 Boolean? 값을 isLoggedIn StateFlow 로 변환한다.
//   null  → 초기 상태 (Firebase 인증 확인 중, SplashScreen 유지)
//   true  → 로그인 상태 (Route.MAIN 으로 전체 스택 교체)
//   false → 미로그인 상태 (Route.Login.GRAPH 로 전체 스택 교체)
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var observeAuthStateUseCase: ObserveAuthStateUseCase
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        observeAuthStateUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = MainViewModel(
            observeAuthStateUseCase = observeAuthStateUseCase,
            mainDispatcher = Dispatchers.Main as MainCoroutineDispatcher,
            defaultDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
        )
    }

    // ── 초기 상태 ──

    @Test
    fun `구독 전 isLoggedIn 초기값은 null이다`() {
        // WhileSubscribed 정책으로 구독자가 없을 때는 initialValue(null) 를 반환
        // → AppNavigation 이 null 을 받으면 SplashScreen 을 유지한다
        every { observeAuthStateUseCase() } returns flowOf(true)
        createViewModel()

        assertNull(viewModel.isLoggedIn.value)
    }

    // ── WhileSubscribed 플로우 (backgroundScope + advanceUntilIdle 패턴) ──

    @Test
    fun `구독 후 isLoggedIn이 true를 방출하면 로그인 상태가 된다`() = runTest {
        // Firebase 에 로그인된 사용자가 있을 때 → AppNavigation 이 MAIN 으로 이동
        every { observeAuthStateUseCase() } returns flowOf(true)
        createViewModel()

        backgroundScope.launch { viewModel.isLoggedIn.collect {} }
        advanceUntilIdle()

        assertTrue(viewModel.isLoggedIn.value!!)
    }

    @Test
    fun `구독 후 isLoggedIn이 false를 방출하면 미로그인 상태가 된다`() = runTest {
        // Firebase 에 로그인된 사용자가 없을 때 → AppNavigation 이 Login.GRAPH 로 이동
        every { observeAuthStateUseCase() } returns flowOf(false)
        createViewModel()

        backgroundScope.launch { viewModel.isLoggedIn.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.isLoggedIn.value!!)
    }

    @Test
    fun `구독 후 isLoggedIn이 null을 방출하면 인증 확인 중 상태가 된다`() = runTest {
        // Firebase 인증 상태를 아직 가져오지 못한 경우 → SplashScreen 유지
        every { observeAuthStateUseCase() } returns flowOf(null)
        createViewModel()

        backgroundScope.launch { viewModel.isLoggedIn.collect {} }
        advanceUntilIdle()

        assertNull(viewModel.isLoggedIn.value)
    }

    @Test
    fun `인증 상태가 null에서 true로 변경되면 isLoggedIn이 갱신된다`() = runTest {
        // 앱 시작 시 null(확인 중) → true(로그인 완료) 로 전환되는 시나리오
        every { observeAuthStateUseCase() } returns flowOf(null, true)
        createViewModel()

        val collected = mutableListOf<Boolean?>()
        backgroundScope.launch { viewModel.isLoggedIn.collect { collected.add(it) } }
        advanceUntilIdle()

        // 마지막으로 방출된 값이 true 이어야 한다
        assertTrue(collected.last()!!)
    }

    @Test
    fun `로그인 후 로그아웃하면 isLoggedIn이 false로 변경된다`() = runTest {
        // true(로그인) → false(로그아웃) 전환 시나리오
        every { observeAuthStateUseCase() } returns flowOf(true, false)
        createViewModel()

        val collected = mutableListOf<Boolean?>()
        backgroundScope.launch { viewModel.isLoggedIn.collect { collected.add(it) } }
        advanceUntilIdle()

        // 최종 상태가 false(미로그인) 이어야 한다
        assertFalse(collected.last()!!)
    }
}
