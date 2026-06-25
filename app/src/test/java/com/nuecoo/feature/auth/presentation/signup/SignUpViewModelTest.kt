package com.nuecoo.feature.auth.presentation.signup

import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.PwCheckResult
import com.nuecoo.feature.auth.domain.model.SignUpResult
import com.nuecoo.feature.auth.domain.usecase.CheckEmailExistsUseCase
import com.nuecoo.feature.auth.domain.usecase.SendVerificationCodeUseCase
import com.nuecoo.feature.auth.domain.usecase.SignUpUseCase
import com.nuecoo.feature.auth.domain.usecase.VerifySmsCodeUseCase
import com.nuecoo.feature.auth.presentation.signup.viewmodel.SignUpViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
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
// SignUpViewModel
// 7단계 회원가입 흐름의 입력값·유효성·결과 상태를 관리하는 ViewModel 테스트
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var checkEmailExistsUseCase: CheckEmailExistsUseCase
    private lateinit var sendVerificationCodeUseCase: SendVerificationCodeUseCase
    private lateinit var verifySmsCodeUseCase: VerifySmsCodeUseCase
    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        checkEmailExistsUseCase = mockk(relaxed = true)
        sendVerificationCodeUseCase = mockk(relaxed = true)
        verifySmsCodeUseCase = mockk(relaxed = true)
        signUpUseCase = mockk()
        viewModel = SignUpViewModel(
            checkEmailExistsUseCase = checkEmailExistsUseCase,
            sendVerificationCodeUseCase = sendVerificationCodeUseCase,
            verifySmsCodeUseCase = verifySmsCodeUseCase,
            signUpUseCase = signUpUseCase,
            mainDispatcher = Dispatchers.Main as MainCoroutineDispatcher,
            defaultDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── 회원가입 단계(signUpStep) ──

    @Test
    fun `초기 회원가입 단계는 0이다`() {
        // ViewModel 생성 직후 첫 번째 단계(이용약관)부터 시작
        assertEquals(0, viewModel.signUpStep.value)
    }

    @Test
    fun `updateSignUpStep 호출 시 단계가 업데이트된다`() {
        // 다음 단계로 이동하면 signUpStep 값이 변경되어야 함
        viewModel.updateSignUpStep(3)
        assertEquals(3, viewModel.signUpStep.value)
    }

    // ── 이용약관 동의(Terms) ──

    @Test
    fun `아무것도 체크하지 않으면 전체 동의가 false이다`() = runTest {
        // 개인정보와 이용약관 모두 체크 전에는 전체 동의 불가
        // WhileSubscribed 활성화: launch 후 advanceUntilIdle()로 구독 코루틴을 실제 실행시켜야 combine이 동작함
        backgroundScope.launch { viewModel.isAllTermsChecked.collect {} }
        advanceUntilIdle()
        assertFalse(viewModel.isAllTermsChecked.value)
    }

    @Test
    fun `개인정보만 체크하면 전체 동의가 false이다`() = runTest {
        // 이용약관이 빠지면 전체 동의 조건 미충족
        backgroundScope.launch { viewModel.isAllTermsChecked.collect {} }
        advanceUntilIdle()
        viewModel.setCheckedPrivacy(true)
        assertFalse(viewModel.isAllTermsChecked.value)
    }

    @Test
    fun `이용약관만 체크하면 전체 동의가 false이다`() = runTest {
        // 개인정보가 빠지면 전체 동의 조건 미충족
        backgroundScope.launch { viewModel.isAllTermsChecked.collect {} }
        advanceUntilIdle()
        viewModel.setCheckedTerms(true)
        assertFalse(viewModel.isAllTermsChecked.value)
    }

    @Test
    fun `개인정보와 이용약관을 모두 체크하면 전체 동의가 true이다`() = runTest {
        // 두 항목 모두 체크하면 전체 동의 충족
        backgroundScope.launch { viewModel.isAllTermsChecked.collect {} }
        advanceUntilIdle()
        viewModel.setCheckedPrivacy(true)
        viewModel.setCheckedTerms(true)
        assertTrue(viewModel.isAllTermsChecked.value)
    }

    @Test
    fun `setAllTermsChecked true 호출 시 두 항목이 모두 true가 된다`() = runTest {
        // '전체 동의' 버튼을 눌렀을 때 개인정보·이용약관 동시에 체크
        backgroundScope.launch { viewModel.isAllTermsChecked.collect {} }
        advanceUntilIdle()
        viewModel.setAllTermsChecked(true)
        assertTrue(viewModel.checkedPrivacy.value)
        assertTrue(viewModel.checkedTerms.value)
        assertTrue(viewModel.isAllTermsChecked.value)
    }

    @Test
    fun `setAllTermsChecked false 호출 시 두 항목이 모두 false가 된다`() = runTest {
        // '전체 동의' 해제 버튼을 눌렀을 때 개인정보·이용약관 동시에 해제
        backgroundScope.launch { viewModel.isAllTermsChecked.collect {} }
        advanceUntilIdle()
        viewModel.setAllTermsChecked(true)
        viewModel.setAllTermsChecked(false)
        assertFalse(viewModel.checkedPrivacy.value)
        assertFalse(viewModel.checkedTerms.value)
        assertFalse(viewModel.isAllTermsChecked.value)
    }

    // ── 비밀번호 유효성 검사(checkPw) ──

    // 영문+숫자+특수문자 조합 8~32자를 만족하는 유효한 비밀번호
    private val validPw = "Password1!"
    // 형식을 만족하지 못하는 짧은 비밀번호
    private val invalidPw = "short"

    @Test
    fun `비밀번호 형식이 맞지 않으면 NotValid를 반환한다`() {
        // 영문+숫자+특수문자 조합 8~32자 조건 미충족 시
        viewModel.setPw(invalidPw)
        viewModel.setPwCheck(invalidPw)

        viewModel.checkPw()

        assertEquals(PwCheckResult.NotValid, viewModel.isPwResult.value)
    }

    @Test
    fun `비밀번호와 확인 비밀번호가 다르면 NotAccordance를 반환한다`() {
        // 형식은 맞지만 두 입력값이 서로 다른 경우
        viewModel.setPw(validPw)
        viewModel.setPwCheck("Password2!")

        viewModel.checkPw()

        assertEquals(PwCheckResult.NotAccordance, viewModel.isPwResult.value)
    }

    @Test
    fun `형식이 맞고 두 비밀번호가 일치하면 Success를 반환한다`() {
        // 정상적인 비밀번호 설정 시나리오
        viewModel.setPw(validPw)
        viewModel.setPwCheck(validPw)

        viewModel.checkPw()

        assertEquals(PwCheckResult.Success, viewModel.isPwResult.value)
    }

    @Test
    fun `확인 비밀번호만 형식이 맞지 않아도 NotValid를 반환한다`() {
        // pw 는 유효하지만 pwCheck 가 형식 위반이면 NotValid
        viewModel.setPw(validPw)
        viewModel.setPwCheck(invalidPw)

        viewModel.checkPw()

        assertEquals(PwCheckResult.NotValid, viewModel.isPwResult.value)
    }

    // ── 생년월일 포맷(Birth formatting) ──

    @Test
    fun `trySignUp 호출 시 생년월일이 yyyyMMdd 형식으로 조립된다`() = runTest {
        // 연·월·일을 따로 입력받아 하나의 문자열로 합칠 때 자릿수 패딩이 정확해야 함
        val captured = slot<AuthModel>()
        coEvery { signUpUseCase(capture(captured)) } returns SignUpResult.Success

        viewModel.setYear(1995)
        viewModel.setMonth(3)  // 한 자리 월 → "03"
        viewModel.setDay(7)    // 한 자리 일 → "07"
        viewModel.setPw(validPw)
        viewModel.setPwCheck(validPw)

        viewModel.trySignUp()

        assertEquals("19950307", captured.captured.birth)
    }

    // ── 회원가입 실행(trySignUp) ──

    @Test
    fun `trySignUp 호출 시 signUpUseCase가 정확히 한 번 호출된다`() = runTest {
        // ViewModel 이 UseCase 에 정확히 한 번 위임하는지 확인
        coEvery { signUpUseCase(any()) } returns SignUpResult.Success

        viewModel.setEmail("test@email.com")
        viewModel.setPw(validPw)
        viewModel.setNickname("닉네임")
        viewModel.setPhone("01012345678")
        viewModel.setGender(true)
        viewModel.setYear(2000)
        viewModel.setMonth(1)
        viewModel.setDay(1)

        viewModel.trySignUp()

        coVerify(exactly = 1) { signUpUseCase(any()) }
    }

    @Test
    fun `signUpUseCase가 Success를 반환하면 isSignupResult가 Success이다`() = runTest {
        // 회원가입 성공 시 결과 상태 반영
        coEvery { signUpUseCase(any()) } returns SignUpResult.Success

        viewModel.trySignUp()

        assertEquals(SignUpResult.Success, viewModel.isSignupResult.value)
    }

    @Test
    fun `signUpUseCase가 AlreadyExists를 반환하면 isSignupResult가 AlreadyExists이다`() = runTest {
        // 중복 이메일로 가입 실패 시 결과 상태 반영
        coEvery { signUpUseCase(any()) } returns SignUpResult.AlreadyExists

        viewModel.trySignUp()

        assertEquals(SignUpResult.AlreadyExists, viewModel.isSignupResult.value)
    }

    @Test
    fun `signUpUseCase가 WeakPassword를 반환하면 isSignupResult가 WeakPassword이다`() = runTest {
        // Firebase 에서 비밀번호 강도 부족으로 반려 시 결과 상태 반영
        coEvery { signUpUseCase(any()) } returns SignUpResult.WeakPassword

        viewModel.trySignUp()

        assertEquals(SignUpResult.WeakPassword, viewModel.isSignupResult.value)
    }

    // ── 휴대폰·인증 코드 입력 ──

    @Test
    fun `setPhone 호출 시 앞뒤 공백이 제거된다`() {
        // 전화번호 입력 시 공백 trim 처리
        viewModel.setPhone("  01012345678  ")
        assertEquals("01012345678", viewModel.phone.value)
    }

    @Test
    fun `sendCode 호출 후 isCodeSent가 true가 된다`() = runTest {
        // 인증 코드 전송 요청 후 전송 완료 상태로 변경
        viewModel.sendCode()
        assertTrue(viewModel.isCodeSent.value)
    }

    @Test
    fun `checkCode 호출 후 isPhoneOk가 true가 된다`() = runTest {
        // 인증 코드 확인 후 휴대폰 인증 완료 상태로 변경
        viewModel.checkCode()
        assertTrue(viewModel.isPhoneOk.value == true)
    }

    // ── 닉네임 ──

    @Test
    fun `setNickname 호출 시 닉네임 상태가 업데이트된다`() {
        // 닉네임 입력 값이 상태에 반영되어야 함
        viewModel.setNickname("홍길동")
        assertEquals("홍길동", viewModel.nickname.value)
    }

    // ── 성별·생년월일 ──

    @Test
    fun `setGender 호출 시 성별 상태가 업데이트된다`() {
        // 성별 선택 값이 상태에 반영되어야 함
        viewModel.setGender(true)
        assertTrue(viewModel.gender.value == true)
    }

    @Test
    fun `초기 연도 기본값은 2000이다`() {
        // 생년월일 스피너의 연도 초기값 확인
        assertEquals(2000, viewModel.year.value)
    }

    @Test
    fun `setYear 호출 시 연도 상태가 업데이트된다`() {
        // 연도 선택 값이 상태에 반영되어야 함
        viewModel.setYear(1990)
        assertEquals(1990, viewModel.year.value)
    }
}
