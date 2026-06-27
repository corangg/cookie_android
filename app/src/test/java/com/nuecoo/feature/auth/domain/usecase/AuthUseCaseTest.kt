package com.nuecoo.feature.auth.domain.usecase

import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.SignUpResult
import com.nuecoo.feature.auth.domain.model.VerificationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────
// SignUpUseCase
// 회원가입 요청을 Repository 에 위임하는 UseCase 테스트
// ──────────────────────────────────────────────

class SignUpUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: SignUpUseCase

    // 테스트 전용 회원가입 데이터
    private val dummyAuthModel = AuthModel(
        email = "test@email.com",
        password = "Password1!",
        nickname = "테스터",
        phone = "01012345678",
        birth = "19990101",
        gender = true,
    )

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SignUpUseCase(repository)
    }

    @Test
    fun `UseCase 호출 시 Repository의 trySignUp을 정확히 한 번 호출한다`() = runTest {
        // UseCase 는 직접 로직을 수행하지 않고 Repository 에 위임만 해야 함
        coEvery { repository.trySignUp(dummyAuthModel) } returns SignUpResult.Success

        useCase(dummyAuthModel)

        coVerify(exactly = 1) { repository.trySignUp(dummyAuthModel) }
    }

    @Test
    fun `Repository가 Success를 반환하면 UseCase도 Success를 반환한다`() = runTest {
        // 회원가입 성공 시나리오
        coEvery { repository.trySignUp(any()) } returns SignUpResult.Success

        val result = useCase(dummyAuthModel)

        assertEquals(SignUpResult.Success, result)
    }

    @Test
    fun `Repository가 AlreadyExists를 반환하면 UseCase도 AlreadyExists를 반환한다`() = runTest {
        // 이미 가입된 이메일로 시도한 경우
        coEvery { repository.trySignUp(any()) } returns SignUpResult.AlreadyExists

        val result = useCase(dummyAuthModel)

        assertEquals(SignUpResult.AlreadyExists, result)
    }

    @Test
    fun `Repository가 WeakPassword를 반환하면 UseCase도 WeakPassword를 반환한다`() = runTest {
        // Firebase 가 비밀번호 강도 부족으로 거부한 경우
        coEvery { repository.trySignUp(any()) } returns SignUpResult.WeakPassword

        val result = useCase(dummyAuthModel)

        assertEquals(SignUpResult.WeakPassword, result)
    }

    @Test
    fun `Repository가 DbSaveFailed를 반환하면 UseCase도 DbSaveFailed를 반환한다`() = runTest {
        // 인증은 성공했지만 DB 저장이 실패한 경우
        coEvery { repository.trySignUp(any()) } returns SignUpResult.DbSaveFailed

        val result = useCase(dummyAuthModel)

        assertEquals(SignUpResult.DbSaveFailed, result)
    }

    @Test
    fun `Repository가 Failed를 반환하면 UseCase도 Failed를 반환한다`() = runTest {
        // 알 수 없는 오류로 회원가입이 실패한 경우
        coEvery { repository.trySignUp(any()) } returns SignUpResult.Failed

        val result = useCase(dummyAuthModel)

        assertEquals(SignUpResult.Failed, result)
    }
}

// ──────────────────────────────────────────────
// ObserveAuthStateUseCase
// Firebase 인증 상태를 Flow<Boolean?> 로 관찰하는 UseCase 테스트
// MainViewModel 이 이 UseCase 를 통해 로그인 상태를 실시간으로 파악하고
// AppNavigation 에서 화면 전환에 활용한다.
// ──────────────────────────────────────────────

class ObserveAuthStateUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: ObserveAuthStateUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObserveAuthStateUseCase(repository)
    }

    @Test
    fun `UseCase 호출 시 Repository의 observeAuthState를 위임한다`() {
        // 직접 로직을 수행하지 않고 Repository 에 위임만 해야 함
        every { repository.observeAuthState() } returns flowOf(true)

        useCase()

        verify(exactly = 1) { repository.observeAuthState() }
    }

    @Test
    fun `Repository가 true를 방출하면 UseCase도 true를 방출한다`() = runTest {
        // Firebase 에 로그인된 사용자가 있는 상태
        every { repository.observeAuthState() } returns flowOf(true)

        val result = useCase().toList()

        assertEquals(listOf(true), result)
    }

    @Test
    fun `Repository가 false를 방출하면 UseCase도 false를 방출한다`() = runTest {
        // Firebase 에 로그인된 사용자가 없는 상태 (로그아웃 후)
        every { repository.observeAuthState() } returns flowOf(false)

        val result = useCase().toList()

        assertEquals(listOf(false), result)
    }

    @Test
    fun `Repository가 null을 방출하면 UseCase도 null을 방출한다`() = runTest {
        // Firebase 인증 상태를 아직 확인 중인 초기 상태
        every { repository.observeAuthState() } returns flowOf(null)

        val result = useCase().toList()

        assertEquals(listOf(null), result)
    }

    @Test
    fun `인증 상태 변화 시퀀스를 순서대로 방출한다`() = runTest {
        // null(초기) → true(로그인) → false(로그아웃) 순서로 상태가 변화하는 시나리오
        every { repository.observeAuthState() } returns flowOf(null, true, false)

        val result = useCase().toList()

        assertEquals(listOf(null, true, false), result)
    }
}

// ──────────────────────────────────────────────
// LoginUseCase
// 이메일·비밀번호 로그인을 Repository 에 위임하는 UseCase 테스트
// 이전에는 항상 true 를 반환하는 스텁이었으나 실제 Repository 를 호출하도록 변경됨
// ──────────────────────────────────────────────

class LoginUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: LoginUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = LoginUseCase(repository)
    }

    @Test
    fun `UseCase 호출 시 Repository의 logIn을 정확히 한 번 호출한다`() = runTest {
        // UseCase 는 직접 로직을 수행하지 않고 Repository 에 위임만 해야 함
        coEvery { repository.logIn(any(), any()) } returns true

        useCase("test@email.com", "password1!")

        coVerify(exactly = 1) { repository.logIn("test@email.com", "password1!") }
    }

    @Test
    fun `Repository가 true를 반환하면 UseCase도 true를 반환한다`() = runTest {
        // Firebase 로그인 성공 시나리오
        coEvery { repository.logIn(any(), any()) } returns true

        val result = useCase("test@email.com", "password1!")

        assertTrue(result)
    }

    @Test
    fun `Repository가 false를 반환하면 UseCase도 false를 반환한다`() = runTest {
        // 잘못된 자격증명 또는 네트워크 오류로 로그인 실패한 시나리오
        coEvery { repository.logIn(any(), any()) } returns false

        val result = useCase("test@email.com", "wrongPassword")

        assertFalse(result)
    }

    @Test
    fun `이메일과 비밀번호가 Repository에 그대로 전달된다`() = runTest {
        // UseCase 가 파라미터를 가공하지 않고 그대로 Repository 에 넘겨야 함
        var capturedEmail = ""
        var capturedPassword = ""
        coEvery { repository.logIn(any(), any()) } coAnswers {
            capturedEmail = firstArg()
            capturedPassword = secondArg()
            true
        }

        useCase("user@example.com", "Secret123!")

        assertEquals("user@example.com", capturedEmail)
        assertEquals("Secret123!", capturedPassword)
    }
}

// ──────────────────────────────────────────────
// SendSignUpPhoneCodeUseCase
// 국내 전화번호를 E164 국제 형식으로 변환 후 SMS 인증코드 발송을 요청하는 UseCase 테스트
// ──────────────────────────────────────────────

class SendSignUpPhoneCodeUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: SendSignUpPhoneCodeUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SendSignUpPhoneCodeUseCase(repository)
    }

    @Test
    fun `성공 시 Success를 반환한다`() = runTest {
        // Solapi SMS 발송 성공 → 인증번호 입력 UI 노출 조건
        coEvery { repository.sendSignupVerificationCode(any()) } returns VerificationResult.Success

        val result = useCase("01012345678")

        assertEquals(VerificationResult.Success, result)
    }

    @Test
    fun `이미 가입된 번호이면 AlreadyRegistered를 반환한다`() = runTest {
        // 중복 가입 방지: 서버에서 ALREADY_EXISTS 오류가 내려온 경우
        coEvery { repository.sendSignupVerificationCode(any()) } returns VerificationResult.AlreadyRegistered

        val result = useCase("01012345678")

        assertEquals(VerificationResult.AlreadyRegistered, result)
    }

    @Test
    fun `재발송 한도 초과 시 TooManyAttempts를 반환한다`() = runTest {
        // 쿨다운·횟수 초과: 서버에서 RESOURCE_EXHAUSTED 오류가 내려온 경우
        coEvery { repository.sendSignupVerificationCode(any()) } returns VerificationResult.TooManyAttempts

        val result = useCase("01012345678")

        assertEquals(VerificationResult.TooManyAttempts, result)
    }

    @Test
    fun `SMS 발송 실패 시 SmsSendFailed를 반환한다`() = runTest {
        // Solapi 연동 오류: 서버에서 INTERNAL 오류가 내려온 경우
        coEvery { repository.sendSignupVerificationCode(any()) } returns VerificationResult.SmsSendFailed

        val result = useCase("01012345678")

        assertEquals(VerificationResult.SmsSendFailed, result)
    }

    @Test
    fun `국내 번호를 E164 형식으로 변환하여 Repository에 전달한다`() = runTest {
        // 010-1234-5678 → +821012345678 변환 후 전달
        val capturedPhone = slot<String>()
        coEvery { repository.sendSignupVerificationCode(capture(capturedPhone)) } returns VerificationResult.Success

        useCase("01012345678")

        assertEquals("+821012345678", capturedPhone.captured)
    }

    @Test
    fun `하이픈이 포함된 번호도 E164 형식으로 변환된다`() = runTest {
        // 사용자가 010-1234-5678 형태로 입력해도 올바르게 변환
        val capturedPhone = slot<String>()
        coEvery { repository.sendSignupVerificationCode(capture(capturedPhone)) } returns VerificationResult.Success

        useCase("010-1234-5678")

        assertEquals("+821012345678", capturedPhone.captured)
    }

    @Test
    fun `Repository의 sendVerificationCode를 정확히 한 번 호출한다`() = runTest {
        // UseCase 가 중복 호출 없이 위임하는지 확인
        coEvery { repository.sendSignupVerificationCode(any()) } returns VerificationResult.Success

        useCase("01012345678")

        coVerify(exactly = 1) { repository.sendSignupVerificationCode(any()) }
    }
}

// ──────────────────────────────────────────────
// VerifyCodeUseCase
// 국내 전화번호를 E164 형식으로 변환 후 인증번호 확인을 요청하는 UseCase 테스트
// ──────────────────────────────────────────────

class VerifyCodeUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: VerifySignUpCodeUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = VerifySignUpCodeUseCase(repository)
    }

    @Test
    fun `인증번호 일치 시 Success를 반환한다`() = runTest {
        // 사용자가 올바른 인증번호를 입력한 경우
        coEvery { repository.verifyCodeForSignUp(any(), any()) } returns VerificationResult.Success

        val result = useCase("01012345678", "123456")

        assertEquals(VerificationResult.Success, result)
    }

    @Test
    fun `인증번호 불일치 시 CodeMismatch를 반환한다`() = runTest {
        // 잘못된 인증번호 입력: 서버에서 PERMISSION_DENIED 오류가 내려온 경우
        coEvery { repository.verifyCodeForSignUp(any(), any()) } returns VerificationResult.CodeMismatch

        val result = useCase("01012345678", "000000")

        assertEquals(VerificationResult.CodeMismatch, result)
    }

    @Test
    fun `인증번호 만료 시 CodeExpired를 반환한다`() = runTest {
        // 3분 유효시간 초과: 서버에서 DEADLINE_EXCEEDED 오류가 내려온 경우
        coEvery { repository.verifyCodeForSignUp(any(), any()) } returns VerificationResult.CodeExpired

        val result = useCase("01012345678", "123456")

        assertEquals(VerificationResult.CodeExpired, result)
    }

    @Test
    fun `인증 요청 내역이 없으면 RequestNotFound를 반환한다`() = runTest {
        // sendCode 를 거치지 않고 checkCode 를 먼저 호출한 경우: 서버에서 NOT_FOUND
        coEvery { repository.verifyCodeForSignUp(any(), any()) } returns VerificationResult.RequestNotFound

        val result = useCase("01012345678", "123456")

        assertEquals(VerificationResult.RequestNotFound, result)
    }

    @Test
    fun `국내 번호를 E164 형식으로 변환하여 Repository에 전달한다`() = runTest {
        // 전화번호 변환 검증: 010-1234-5678 → +821012345678
        val capturedPhone = slot<String>()
        coEvery { repository.verifyCodeForSignUp(capture(capturedPhone), any()) } returns VerificationResult.Success

        useCase("01012345678", "123456")

        assertEquals("+821012345678", capturedPhone.captured)
    }

    @Test
    fun `인증번호가 Repository에 그대로 전달된다`() = runTest {
        // UseCase 가 code 파라미터를 가공하지 않고 그대로 전달해야 함
        val capturedCode = slot<String>()
        coEvery { repository.verifyCodeForSignUp(any(), capture(capturedCode)) } returns VerificationResult.Success

        useCase("01012345678", "987654")

        assertEquals("987654", capturedCode.captured)
    }

    @Test
    fun `Repository의 verifyCode를 정확히 한 번 호출한다`() = runTest {
        // UseCase 가 중복 호출 없이 위임하는지 확인
        coEvery { repository.verifyCodeForSignUp(any(), any()) } returns VerificationResult.Success

        useCase("01012345678", "123456")

        coVerify(exactly = 1) { repository.verifyCodeForSignUp(any(), any()) }
    }
}
