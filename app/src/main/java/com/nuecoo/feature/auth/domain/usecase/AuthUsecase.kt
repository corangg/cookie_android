package com.nuecoo.feature.auth.domain.usecase

import com.nuecoo.core.domain.repository.DataRepository
import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.SignUpResult
import com.nuecoo.feature.auth.domain.model.VerificationResult
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.observeAuthState()
}

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(authData: AuthModel): SignUpResult{
        val data = authData.copy(
            phone = toE164Format(authData.phone)
        )
        return repository.trySignUp(data)
    }

    private fun toE164Format(domesticPhone: String): String {
        val digitsOnly = domesticPhone.filter { it.isDigit() }
        return "+82" + digitsOnly.removePrefix("0")
    }
}

class CheckEmailExistsUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(email: String) = repository.checkEmailExists(email)
}

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) = repository.logIn(email, password)
}

class SendSignUpPhoneCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String): VerificationResult {
        return repository.sendSignupVerificationCode(toE164Format(phoneNumber))
    }

    private fun toE164Format(domesticPhone: String): String {
        val digitsOnly = domesticPhone.filter { it.isDigit() }
        return "+82" + digitsOnly.removePrefix("0")
    }
}

class VerifySignUpCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, code: String): VerificationResult {
        return repository.verifyCodeForSignUp(toE164Format(phoneNumber), code)
    }

    private fun toE164Format(domesticPhone: String): String {
        val digitsOnly = domesticPhone.filter { it.isDigit() }
        return "+82" + digitsOnly.removePrefix("0")
    }
}

class SendFindEmailPhoneCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String) =
        repository.sendFindEmailVerificationCode(toE164Format(phoneNumber))

    private fun toE164Format(domesticPhone: String): String {
        val digitsOnly = domesticPhone.filter { it.isDigit() }
        return "+82" + digitsOnly.removePrefix("0")
    }
}

class VerifyFindEmailCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, code: String) =
        repository.verifyCodeForFindEmail(toE164Format(phoneNumber), code)

    private fun toE164Format(domesticPhone: String): String {
        val digitsOnly = domesticPhone.filter { it.isDigit() }
        return "+82" + digitsOnly.removePrefix("0")
    }
}

class SendResetPasswordPhoneCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String) =
        repository.sendResetPasswordVerificationCode(toE164Format(phoneNumber))

    private fun toE164Format(domesticPhone: String): String {
        val digitsOnly = domesticPhone.filter { it.isDigit() }
        return "+82" + digitsOnly.removePrefix("0")
    }
}

class VerifyResetPwCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, code: String) =
        repository.verifyCodeForResetPassword(toE164Format(phoneNumber), code)

    private fun toE164Format(domesticPhone: String): String {
        val digitsOnly = domesticPhone.filter { it.isDigit() }
        return "+82" + digitsOnly.removePrefix("0")
    }
}

class ResetPwUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, password: String) = repository.resetPassword(toE164Format(phoneNumber), password)

    private fun toE164Format(domesticPhone: String): String {
        val digitsOnly = domesticPhone.filter { it.isDigit() }
        return "+82" + digitsOnly.removePrefix("0")
    }
}