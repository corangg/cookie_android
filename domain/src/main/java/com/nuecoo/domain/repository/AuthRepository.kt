package com.nuecoo.domain.repository

interface AuthRepository {
    suspend fun isLoggedIn(): Boolean
    suspend fun login(email: String, password: String): Boolean
    suspend fun logout(): Boolean
    suspend fun checkEmailExists(email: String): Boolean
    suspend fun sendVerificationCode(phoneNumber: String): String
    suspend fun verifySmsCode(verificationId: String, code: String): Boolean
    suspend fun signUp(
        email: String,
        password: String,
        verificationId: String,
        smsCode: String,
        phone: String,
        gender: Boolean,
        birth: String
    ): Boolean
}
