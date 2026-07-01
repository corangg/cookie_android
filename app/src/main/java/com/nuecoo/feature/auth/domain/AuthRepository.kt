package com.nuecoo.feature.auth.domain

import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.FindEmailResult
import com.nuecoo.feature.auth.domain.model.SignUpResult
import com.nuecoo.feature.auth.domain.model.UserInfo
import com.nuecoo.feature.auth.domain.model.VerificationResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthState(): Flow<Boolean?>
    suspend fun trySignUp(authModel: AuthModel): SignUpResult
    suspend fun logIn(email: String, password: String): Boolean
    suspend fun logOut(): Boolean
    suspend fun sendSignupVerificationCode(phoneNumber: String): VerificationResult
    suspend fun verifyCodeForSignUp(phoneNumber: String, code: String): VerificationResult
    suspend fun sendFindEmailVerificationCode(phoneNumber: String): VerificationResult
    suspend fun verifyCodeForFindEmail(phoneNumber: String, code: String): FindEmailResult
    suspend fun sendResetPasswordVerificationCode(phoneNumber: String): VerificationResult
    suspend fun verifyCodeForResetPassword(phoneNumber: String, code: String): VerificationResult
    suspend fun resetPassword(phoneNumber: String, newPassword: String): VerificationResult
    suspend fun refreshUserInfo()
    fun observeUserInfo(): Flow<UserInfo?>
}