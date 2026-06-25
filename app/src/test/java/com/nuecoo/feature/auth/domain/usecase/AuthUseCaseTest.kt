package com.nuecoo.feature.auth.domain.usecase

import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.SignUpResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
