package com.nuecoo.feature.auth.domain

import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.SignUpResult
import com.nuecoo.feature.auth.domain.model.SignUpVerificationResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthState(): Flow<Boolean?>
    suspend fun trySignUp(authModel: AuthModel): SignUpResult
    suspend fun logIn(email: String, password: String): Boolean
    suspend fun logOut(): Boolean
    suspend fun sendVerificationCode(phoneNumber: String): SignUpVerificationResult
    suspend fun verifyCode(phoneNumber: String, code: String): SignUpVerificationResult


    /* suspend fun isLoggedIn(): Boolean
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
     ): Boolean*/
}