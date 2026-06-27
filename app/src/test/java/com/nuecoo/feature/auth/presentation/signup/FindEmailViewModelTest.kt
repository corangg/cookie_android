package com.nuecoo.feature.auth.presentation.signup

import com.nuecoo.feature.auth.domain.model.FindEmailResult
import com.nuecoo.feature.auth.domain.model.VerificationResult
import com.nuecoo.feature.auth.domain.usecase.SendFindEmailPhoneCodeUseCase
import com.nuecoo.feature.auth.domain.usecase.VerifyFindEmailCodeUseCase
import com.nuecoo.feature.auth.presentation.signup.viewmodel.FindEmailViewModel
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
// FindEmailViewModel
// 휴대폰 인증을 통해 가입된 이메일을 찾는 흐름의 입력값·결과 상태를 관리하는 ViewModel 테스트
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class FindEmailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var sendFindEmailPhoneCodeUseCase: SendFindEmailPhoneCodeUseCase
    private lateinit var verifyFindEmailCodeUseCase: VerifyFindEmailCodeUseCase
    private lateinit var viewModel: FindEmailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sendFindEmailPhoneCodeUseCase = mockk()
        verifyFindEmailCodeUseCase = mockk()
        viewModel = FindEmailViewModel(
            sendFindEmailPhoneCodeUseCase = sendFindEmailPhoneCodeUseCase,
            verifyFindEmailCodeUseCase = verifyFindEmailCodeUseCase,
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
        // checkCode 호출 전에는 이메일 찾기 결과가 없어야 함
        assertNull(viewModel.isFindResult.value)
    }

    @Test
    fun `초기 phone과 code는 빈 문자열이다`() {
        // 입력 전 기본값 확인
        assertEquals("", viewModel.phone.value)
        assertEquals("", viewModel.code.value)
    }

    // ── 입력값 관리 ──

    @Test
    fun `setPhone 호출 시 전화번호 상태가 업데이트된다`() {
        // 전화번호 입력 값이 상태에 반영되어야 함
        viewModel.setPhone("01012345678")
        assertEquals("01012345678", viewModel.phone.value)
    }

    @Test
    fun `setCode 호출 시 앞뒤 공백이 제거된다`() {
        // 인증번호 입력 시 공백 trim 처리
        viewModel.setCode("  123456  ")
        assertEquals("123456", viewModel.code.value)
    }

    // ── 인증번호 발송(sendCode) ──

    @Test
    fun `sendCode 성공 시 isCodeSent가 Success이다`() = runTest {
        // SMS 발송 성공 → UI 에서 인증번호 입력창 표시
        coEvery { sendFindEmailPhoneCodeUseCase(any()) } returns VerificationResult.Success

        viewModel.setPhone("01012345678")
        viewModel.sendCode()

        assertEquals(VerificationResult.Success, viewModel.isCodeSent.value)
    }

    @Test
    fun `sendCode 실패 시 isCodeSent에 에러 결과가 반영된다`() = runTest {
        // 재발송 한도 초과 시나리오
        coEvery { sendFindEmailPhoneCodeUseCase(any()) } returns VerificationResult.TooManyAttempts

        viewModel.setPhone("01012345678")
        viewModel.sendCode()

        assertEquals(VerificationResult.TooManyAttempts, viewModel.isCodeSent.value)
    }

    @Test
    fun `sendCode는 SendFindEmailPhoneCodeUseCase에 위임한다`() = runTest {
        // ViewModel 이 직접 SMS 발송 로직을 처리하지 않고 UseCase 에 위임하는지 확인
        coEvery { sendFindEmailPhoneCodeUseCase(any()) } returns VerificationResult.Success

        viewModel.setPhone("01012345678")
        viewModel.sendCode()

        coVerify(exactly = 1) { sendFindEmailPhoneCodeUseCase("01012345678") }
    }

    // ── 인증번호 확인 및 이메일 찾기(checkCode) ──

    @Test
    fun `checkCode 성공 시 isFindResult에 마스킹된 이메일이 반영된다`() = runTest {
        // 인증번호 일치 → 가입된 이메일을 마스킹하여 표시
        val success = FindEmailResult.Success("te****@email.com")
        coEvery { verifyFindEmailCodeUseCase(any(), any()) } returns success

        viewModel.setPhone("01012345678")
        viewModel.setCode("123456")
        viewModel.checkCode()

        assertEquals(success, viewModel.isFindResult.value)
    }

    @Test
    fun `checkCode 실패 시 isFindResult에 Failure가 반영된다`() = runTest {
        // 인증번호 불일치 시 CodeMismatch 사유의 Failure 반환
        val failure = FindEmailResult.Failure(VerificationResult.CodeMismatch)
        coEvery { verifyFindEmailCodeUseCase(any(), any()) } returns failure

        viewModel.setPhone("01012345678")
        viewModel.setCode("000000")
        viewModel.checkCode()

        assertEquals(failure, viewModel.isFindResult.value)
    }

    @Test
    fun `checkCode는 phone과 code를 VerifyFindEmailCodeUseCase에 위임한다`() = runTest {
        // ViewModel 이 직접 인증 로직을 처리하지 않고 UseCase 에 위임하는지 확인
        coEvery {
            verifyFindEmailCodeUseCase(any(), any())
        } returns FindEmailResult.Success("te****@email.com")

        viewModel.setPhone("01012345678")
        viewModel.setCode("123456")
        viewModel.checkCode()

        coVerify(exactly = 1) {
            verifyFindEmailCodeUseCase(phoneNumber = "01012345678", code = "123456")
        }
    }
}
