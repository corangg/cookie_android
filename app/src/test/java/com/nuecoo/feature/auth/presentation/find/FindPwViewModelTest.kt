package com.nuecoo.feature.auth.presentation.find

import com.nuecoo.core.domain.repository.DataRepository
import com.nuecoo.feature.auth.domain.model.PwCheckResult
import com.nuecoo.feature.auth.domain.model.VerificationResult
import com.nuecoo.feature.auth.domain.usecase.CheckEmailExistsUseCase
import com.nuecoo.feature.auth.domain.usecase.ResetPwUseCase
import com.nuecoo.feature.auth.domain.usecase.SendResetPasswordPhoneCodeUseCase
import com.nuecoo.feature.auth.domain.usecase.VerifyResetPwCodeUseCase
import com.nuecoo.feature.auth.presentation.find.viewmodel.FindPwViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
// FindPwViewModel
// 이메일 확인 → 휴대폰 인증 → 새 비밀번호 설정 순서로 진행되는
// 비밀번호 재설정 흐름의 입력값·결과 상태를 관리하는 ViewModel 테스트
//
// 참고: checkEmail()은 android.util.Patterns을 직접 사용하므로
//       Robolectric 없이 JVM 단위 테스트에서는 실행 불가 → 제외
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class FindPwViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var checkEmailExistsUseCase: CheckEmailExistsUseCase
    private lateinit var sendResetPasswordPhoneCodeUseCase: SendResetPasswordPhoneCodeUseCase
    private lateinit var verifyResetPasswordCodeUseCase: VerifyResetPwCodeUseCase
    private lateinit var resetPwUseCase: ResetPwUseCase
    private lateinit var viewModel: FindPwViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        checkEmailExistsUseCase = mockk(relaxed = true)
        sendResetPasswordPhoneCodeUseCase = mockk()
        verifyResetPasswordCodeUseCase = mockk()
        resetPwUseCase = mockk()
        viewModel = FindPwViewModel(
            checkEmailExistsUseCase = checkEmailExistsUseCase,
            sendResetPasswordPhoneCodeUseCase = sendResetPasswordPhoneCodeUseCase,
            verifyResetPasswordCodeUseCase = verifyResetPasswordCodeUseCase,
            resetPwUseCase = resetPwUseCase,
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
    fun `초기 isCodeSent는 null이다`() {
        // sendCode 호출 전에는 전송 결과가 없어야 함
        assertNull(viewModel.isCodeSent.value)
    }

    @Test
    fun `초기 isFindResult는 null이다`() {
        // checkCode 호출 전에는 인증 결과가 없어야 함
        assertNull(viewModel.isFindResult.value)
    }

    @Test
    fun `초기 isPwResult는 null이다`() {
        // checkPw 호출 전에는 비밀번호 재설정 결과가 없어야 함
        assertNull(viewModel.isPwResult.value)
    }

    @Test
    fun `초기 isEmailResult는 null이다`() {
        // checkEmail 호출 전에는 이메일 확인 결과가 없어야 함
        assertNull(viewModel.isEmailResult.value)
    }

    // ── 입력값 관리 ──

    @Test
    fun `setEmail 호출 시 이메일 상태가 업데이트된다`() {
        viewModel.setEmail("test@email.com")
        assertEquals("test@email.com", viewModel.email.value)
    }

    @Test
    fun `setPhone 호출 시 전화번호 상태가 업데이트된다`() {
        viewModel.setPhone("01012345678")
        assertEquals("01012345678", viewModel.phone.value)
    }

    @Test
    fun `setCode 호출 시 인증번호 상태가 업데이트된다`() {
        // FindPwViewModel의 setCode는 trim을 하지 않음
        viewModel.setCode("123456")
        assertEquals("123456", viewModel.code.value)
    }

    @Test
    fun `setPw 호출 시 비밀번호 상태가 업데이트된다`() {
        viewModel.setPw("Password1!")
        assertEquals("Password1!", viewModel.pw.value)
    }

    @Test
    fun `setPwCheck 호출 시 비밀번호 확인 상태가 업데이트된다`() {
        viewModel.setPwCheck("Password1!")
        assertEquals("Password1!", viewModel.pwCheck.value)
    }

    // ── 인증번호 발송(sendCode) ──

    @Test
    fun `sendCode 성공 시 isCodeSent가 Success이다`() = runTest {
        // SMS 발송 성공 → UI 에서 인증번호 입력창 표시
        coEvery { sendResetPasswordPhoneCodeUseCase(any()) } returns VerificationResult.Success

        viewModel.setPhone("01012345678")
        viewModel.sendCode()

        assertEquals(VerificationResult.Success, viewModel.isCodeSent.value)
    }

    @Test
    fun `sendCode 실패 시 isCodeSent에 에러 결과가 반영된다`() = runTest {
        // 재발송 한도 초과 시나리오
        coEvery { sendResetPasswordPhoneCodeUseCase(any()) } returns VerificationResult.TooManyAttempts

        viewModel.setPhone("01012345678")
        viewModel.sendCode()

        assertEquals(VerificationResult.TooManyAttempts, viewModel.isCodeSent.value)
    }

    @Test
    fun `sendCode는 SendResetPasswordPhoneCodeUseCase에 위임한다`() = runTest {
        // ViewModel 이 직접 SMS 발송 로직을 처리하지 않고 UseCase 에 위임하는지 확인
        coEvery { sendResetPasswordPhoneCodeUseCase(any()) } returns VerificationResult.Success

        viewModel.setPhone("01012345678")
        viewModel.sendCode()

        coVerify(exactly = 1) { sendResetPasswordPhoneCodeUseCase("01012345678") }
    }

    // ── 인증번호 확인(checkCode) ──

    @Test
    fun `checkCode 성공 시 isFindResult가 Success이다`() = runTest {
        // 인증번호 일치 → 비밀번호 재설정 단계로 진행 가능
        coEvery { verifyResetPasswordCodeUseCase(any(), any()) } returns VerificationResult.Success

        viewModel.setPhone("01012345678")
        viewModel.setCode("123456")
        viewModel.checkCode()

        assertEquals(VerificationResult.Success, viewModel.isFindResult.value)
    }

    @Test
    fun `checkCode 실패 시 isFindResult에 에러 결과가 반영된다`() = runTest {
        // 인증번호 불일치: 서버에서 PERMISSION_DENIED 오류가 내려온 경우
        coEvery { verifyResetPasswordCodeUseCase(any(), any()) } returns VerificationResult.CodeMismatch

        viewModel.setPhone("01012345678")
        viewModel.setCode("000000")
        viewModel.checkCode()

        assertEquals(VerificationResult.CodeMismatch, viewModel.isFindResult.value)
    }

    @Test
    fun `checkCode 만료 시 isFindResult에 CodeExpired가 반영된다`() = runTest {
        // 3분 유효시간 초과 후 인증 시도
        coEvery { verifyResetPasswordCodeUseCase(any(), any()) } returns VerificationResult.CodeExpired

        viewModel.setPhone("01012345678")
        viewModel.setCode("123456")
        viewModel.checkCode()

        assertEquals(VerificationResult.CodeExpired, viewModel.isFindResult.value)
    }

    @Test
    fun `checkCode는 phone과 code를 VerifyResetPwCodeUseCase에 위임한다`() = runTest {
        // ViewModel 이 직접 인증 로직을 처리하지 않고 UseCase 에 위임하는지 확인
        coEvery { verifyResetPasswordCodeUseCase(any(), any()) } returns VerificationResult.Success

        viewModel.setPhone("01012345678")
        viewModel.setCode("123456")
        viewModel.checkCode()

        coVerify(exactly = 1) {
            verifyResetPasswordCodeUseCase(phoneNumber = "01012345678", code = "123456")
        }
    }

    // ── 비밀번호 유효성 검사 및 재설정(checkPw) ──

    private val validPw = "Password1!"
    private val invalidPw = "short"

    @Test
    fun `비밀번호 형식이 맞지 않으면 NotValid를 반환한다`() = runTest {
        // 영문+숫자+특수문자 조합 8~32자 조건 미충족 시
        viewModel.setPw(invalidPw)
        viewModel.setPwCheck(invalidPw)

        viewModel.checkPw()

        assertEquals(PwCheckResult.NotValid, viewModel.isPwResult.value)
    }

    @Test
    fun `확인 비밀번호만 형식이 맞지 않아도 NotValid를 반환한다`() = runTest {
        // pw 는 유효하지만 pwCheck 가 형식 위반이면 NotValid
        viewModel.setPw(validPw)
        viewModel.setPwCheck(invalidPw)

        viewModel.checkPw()

        assertEquals(PwCheckResult.NotValid, viewModel.isPwResult.value)
    }

    @Test
    fun `형식은 맞지만 비밀번호와 확인 비밀번호가 다르면 NotAccordance를 반환한다`() = runTest {
        // 형식은 유효하지만 두 입력값이 서로 다른 경우
        viewModel.setPw(validPw)
        viewModel.setPwCheck("Different1!")

        viewModel.checkPw()

        assertEquals(PwCheckResult.NotAccordance, viewModel.isPwResult.value)
    }

    @Test
    fun `형식이 맞고 두 비밀번호가 일치하며 재설정 성공 시 isPwResult가 Success이다`() = runTest {
        // 정상 비밀번호 + 서버 재설정 성공 → 재설정 완료
        coEvery { resetPwUseCase(any(), any()) } returns VerificationResult.Success

        viewModel.setPhone("01012345678")
        viewModel.setPw(validPw)
        viewModel.setPwCheck(validPw)
        viewModel.checkPw()

        assertEquals(PwCheckResult.Success, viewModel.isPwResult.value)
    }

    @Test
    fun `형식이 맞고 두 비밀번호가 일치해도 재설정 실패 시 isPwResult가 Error이다`() = runTest {
        // 정상 비밀번호지만 서버 재설정 실패 → Error 반영
        coEvery { resetPwUseCase(any(), any()) } returns VerificationResult.Unknown

        viewModel.setPw(validPw)
        viewModel.setPwCheck(validPw)
        viewModel.checkPw()

        assertEquals(PwCheckResult.Error, viewModel.isPwResult.value)
    }

    @Test
    fun `checkPw 성공 시 ResetPwUseCase에 phone과 pw를 전달한다`() = runTest {
        // ViewModel 이 UseCase 에 전화번호와 새 비밀번호를 그대로 전달하는지 확인
        coEvery { resetPwUseCase(any(), any()) } returns VerificationResult.Success

        viewModel.setPhone("01012345678")
        viewModel.setPw(validPw)
        viewModel.setPwCheck(validPw)
        viewModel.checkPw()

        coVerify(exactly = 1) { resetPwUseCase(phoneNumber = "01012345678", password = validPw) }
    }
}
