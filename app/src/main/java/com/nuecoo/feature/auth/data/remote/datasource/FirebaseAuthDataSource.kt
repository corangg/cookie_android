package com.nuecoo.feature.auth.data.remote.datasource

import com.nuecoo.core.data.model.remote.RemoteAuthModel
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthDataSource {
    fun observeAuthState(): Flow<Boolean?>
    suspend fun trySignUp(authModel: RemoteAuthModel)
    suspend fun logIn(email: String, password: String)
    suspend fun logOut()
    suspend fun sendVerificationCode(phoneNumber: String, purpose: String)
    suspend fun verifyCode(phoneNumber: String, code: String)
    suspend fun verifyCodeAndFindEmail(phoneNumber: String, code: String): String
    suspend fun verifyCodeForResetPassword(phoneNumber: String, code: String)
    suspend fun resetPassword(phoneNumber: String, newPassword: String)
}