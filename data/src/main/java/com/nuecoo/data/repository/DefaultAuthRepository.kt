package com.nuecoo.data.repository

import com.nuecoo.data.datasource.remote.FirebaseAuthDataSource
import com.nuecoo.domain.repository.AuthRepository
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun isLoggedIn(): Boolean = firebaseAuthDataSource.isLoggedIn()

    override suspend fun login(email: String, password: String): Boolean =
        firebaseAuthDataSource.login(email, password)

    override suspend fun logout(): Boolean = firebaseAuthDataSource.logout()

    override suspend fun checkEmailExists(email: String): Boolean =
        firebaseAuthDataSource.checkEmailExists(email)

    override suspend fun sendVerificationCode(phoneNumber: String): String =
        firebaseAuthDataSource.sendVerificationCode(phoneNumber)

    override suspend fun verifySmsCode(verificationId: String, code: String): Boolean =
        firebaseAuthDataSource.verifySmsCode(verificationId, code)

    override suspend fun signUp(
        email: String,
        password: String,
        verificationId: String,
        smsCode: String,
        phone: String,
        gender: Boolean,
        birth: String
    ): Boolean = runCatching {
        val phoneVerified = if (verificationId.isNotEmpty()) {
            firebaseAuthDataSource.verifySmsCode(verificationId, smsCode)
        } else true
        if (!phoneVerified) return@runCatching false
        firebaseAuthDataSource.createAccount(email, password)
    }.getOrElse { false }
}
