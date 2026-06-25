package com.nuecoo.feature.auth.domain.usecase

import com.nuecoo.core.domain.repository.DataRepository
import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.SignUpResult
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(authData: AuthModel): SignUpResult = repository.trySignUp(authData)
}

class CheckEmailExistsUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(email: String) = repository.checkEmailExists(email)
}



class CheckAuthUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = true//repository.isLoggedIn()
}

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Boolean = true
}


class SendVerificationCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String): String = ""
}

class VerifySmsCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(verificationId: String, code: String): Boolean = true
}

