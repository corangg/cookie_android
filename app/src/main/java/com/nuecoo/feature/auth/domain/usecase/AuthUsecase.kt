package com.nuecoo.feature.auth.domain.usecase

import com.nuecoo.domain.repository.AuthRepository
import com.nuecoo.feature.auth.domain.model.AuthModel
import javax.inject.Inject

class CheckAuthUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repository.isLoggedIn()
}

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Boolean =
        repository.login(email, password)
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repository.logout()
}

class CheckEmailExistsUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Boolean = repository.checkEmailExists(email)
}

class SendVerificationCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String): String =
        repository.sendVerificationCode(phoneNumber)
}

class VerifySmsCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(verificationId: String, code: String): Boolean =
        repository.verifySmsCode(verificationId, code)
}

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(authData: AuthModel): Boolean {
        return true
    }
}
