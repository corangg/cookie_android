package com.nuecoo.feature.auth.presentation.login

import com.nuecoo.feature.auth.domain.model.LoginResult
import com.nuecoo.feature.auth.domain.usecase.LoginUseCase
import com.nuecoo.feature.auth.presentation.login.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────
// LoginViewModel
// 이메일·비밀번호 입력 및 로그인 결과 상태를 관리하는 ViewModel 테스트
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        viewModel = LoginViewModel(
            loginUseCase = loginUseCase,
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
    fun `초기 이메일은 빈 문자열이다`() {
        // ViewModel 생성 직후 이메일 입력 필드는 비어있어야 함
        assertEquals("", viewModel.email.value)
    }

    @Test
    fun `초기 비밀번호는 빈 문자열이다`() {
        // ViewModel 생성 직후 비밀번호 입력 필드는 비어있어야 함
        assertEquals("", viewModel.password.value)
    }

    @Test
    fun `초기 loginResult는 null이다`() {
        // 로그인을 시도하기 전에는 결과 상태가 없음
        assertNull(viewModel.loginResult.value)
    }

    // ── setEmail ──

    @Test
    fun `setEmail은 앞뒤 공백을 제거한다`() {
        // 사용자가 실수로 공백을 포함해 입력해도 trim 처리됨
        viewModel.setEmail("  test@email.com  ")
        assertEquals("test@email.com", viewModel.email.value)
    }

    @Test
    fun `setEmail은 이메일 상태를 업데이트한다`() {
        // 정상적인 이메일 입력 시 상태 반영
        viewModel.setEmail("user@test.com")
        assertEquals("user@test.com", viewModel.email.value)
    }

    // ── setPassword ──

    @Test
    fun `setPassword는 공백을 제거하지 않는다`() {
        // 비밀번호는 공백도 유효한 문자이므로 trim 하지 않음
        viewModel.setPassword(" myPassword ")
        assertEquals(" myPassword ", viewModel.password.value)
    }

    // ── login ──

    @Test
    fun `이메일이 비어있으면 로그인 결과가 Empty이다`() = runTest {
        // 이메일을 입력하지 않고 로그인 시도하면 빈 입력 안내
        viewModel.setEmail("")
        viewModel.setPassword("somePass1!")

        viewModel.login()

        assertEquals(LoginResult.Empty, viewModel.loginResult.value)
    }

    @Test
    fun `비밀번호가 비어있으면 로그인 결과가 Empty이다`() = runTest {
        // 비밀번호를 입력하지 않고 로그인 시도하면 빈 입력 안내
        viewModel.setEmail("user@test.com")
        viewModel.setPassword("")

        viewModel.login()

        assertEquals(LoginResult.Empty, viewModel.loginResult.value)
    }

    @Test
    fun `이메일과 비밀번호 모두 비어있으면 로그인 결과가 Empty이다`() = runTest {
        // 둘 다 비어있는 경우에도 Empty 반환
        viewModel.setEmail("")
        viewModel.setPassword("")

        viewModel.login()

        assertEquals(LoginResult.Empty, viewModel.loginResult.value)
    }

    @Test
    fun `UseCase가 true를 반환하면 로그인 결과가 Success이다`() = runTest {
        // 올바른 자격증명으로 로그인 성공한 경우
        coEvery { loginUseCase(any(), any()) } returns true
        viewModel.setEmail("user@test.com")
        viewModel.setPassword("password1!")

        viewModel.login()

        assertEquals(LoginResult.Success, viewModel.loginResult.value)
    }

    @Test
    fun `UseCase가 false를 반환하면 로그인 결과가 Failed이다`() = runTest {
        // 잘못된 자격증명으로 로그인 실패한 경우
        coEvery { loginUseCase(any(), any()) } returns false
        viewModel.setEmail("user@test.com")
        viewModel.setPassword("wrongPass1!")

        viewModel.login()

        assertEquals(LoginResult.Failed, viewModel.loginResult.value)
    }

    @Test
    fun `로그인 시 공백이 제거된 이메일이 UseCase에 전달된다`() = runTest {
        // setEmail 에서 trim된 이메일이 UseCase 호출 시에도 동일하게 넘어가야 함
        var capturedEmail = ""
        coEvery { loginUseCase(any(), any()) } coAnswers {
            capturedEmail = firstArg()
            true
        }
        viewModel.setEmail("  user@test.com  ")
        viewModel.setPassword("pass1!")

        viewModel.login()

        assertEquals("user@test.com", capturedEmail)
    }
}
