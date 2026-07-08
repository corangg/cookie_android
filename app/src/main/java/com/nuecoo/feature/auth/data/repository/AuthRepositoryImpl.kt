package com.nuecoo.feature.auth.data.repository

import com.nuecoo.core.data.datasource.local.UserInfoDataSource
import com.nuecoo.core.data.datasource.remote.FirebaseDataDataSource
import com.nuecoo.core.data.mapper.toDomain
import com.nuecoo.core.data.mapper.toLocal
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.feature.auth.data.mapper.toRemote
import com.nuecoo.feature.auth.data.mapper.toSignUpResult
import com.nuecoo.feature.auth.data.mapper.toVerificationResult
import com.nuecoo.feature.auth.data.remote.VerificationPurpose
import com.nuecoo.feature.auth.data.remote.datasource.FirebaseAuthDataSource
import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.FindEmailResult
import com.nuecoo.feature.auth.domain.model.SignUpResult
import com.nuecoo.feature.auth.domain.model.UserInfo
import com.nuecoo.feature.auth.domain.model.VerificationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @param:RemoteDataSources private val firebaseAuthDataSource: FirebaseAuthDataSource,
    @param:RemoteDataSources private val firebaseDataDataSource: FirebaseDataDataSource,
    @param:LocalDataSources private val userInfoDataSource: UserInfoDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {
    override fun observeAuthState(): Flow<Boolean?> = firebaseAuthDataSource.observeAuthState()

    override suspend fun trySignUp(authModel: AuthModel) = withContext(ioDispatcher) {
        runCatching {
            firebaseAuthDataSource.trySignUp(authModel.toRemote())
            SignUpResult.Success
        }.getOrElse { e -> e.toSignUpResult() }
    }

    override suspend fun logIn(email: String, password: String) = withContext(ioDispatcher) {
        runCatching {
            firebaseAuthDataSource.logIn(email, password)
            true
        }.getOrElse { false }
    }

    override suspend fun logOut() = withContext(ioDispatcher) {
        runCatching {
            firebaseAuthDataSource.logOut()
            true
        }.getOrElse { false }
    }

    override suspend fun sendSignupVerificationCode(phoneNumber: String) =
        sendCode(phoneNumber = phoneNumber, purpose = VerificationPurpose.SIGNUP)

    override suspend fun verifyCodeForSignUp(phoneNumber: String, code: String) =
        withContext(ioDispatcher) {
            runCatching {
                firebaseAuthDataSource.verifyCode(phoneNumber, code)
                VerificationResult.Success
            }.getOrElse { e -> e.toVerificationResult() }
        }

    override suspend fun sendFindEmailVerificationCode(phoneNumber: String) =
        sendCode(phoneNumber = phoneNumber, purpose = VerificationPurpose.FIND_EMAIL)

    override suspend fun verifyCodeForFindEmail(phoneNumber: String, code: String) =
        withContext(ioDispatcher) {
            runCatching {
                val maskedEmail = firebaseAuthDataSource.verifyCodeAndFindEmail(phoneNumber, code)
                FindEmailResult.Success(maskedEmail)
            }.getOrElse { e -> FindEmailResult.Failure(e.toVerificationResult()) }
        }

    override suspend fun sendResetPasswordVerificationCode(phoneNumber: String) =
        sendCode(phoneNumber = phoneNumber, purpose = VerificationPurpose.RESET_PASSWORD)

    override suspend fun verifyCodeForResetPassword(phoneNumber: String, code: String) =
        withContext(ioDispatcher) {
            runCatching {
                firebaseAuthDataSource.verifyCodeForResetPassword(phoneNumber, code)
                VerificationResult.Success
            }.getOrElse { e -> e.toVerificationResult() }
        }

    override suspend fun resetPassword(phoneNumber: String, newPassword: String) =
        withContext(ioDispatcher) {
            runCatching {
                firebaseAuthDataSource.resetPassword(phoneNumber, newPassword)
                VerificationResult.Success
            }.getOrElse { e ->
                e.toVerificationResult()
            }
        }

    private suspend fun sendCode(phoneNumber: String, purpose: String) = withContext(ioDispatcher) {
        runCatching {
            firebaseAuthDataSource.sendVerificationCode(phoneNumber, purpose)
            VerificationResult.Success
        }.getOrElse { e -> e.toVerificationResult() }
    }

    override suspend fun refreshUserInfo() {
        runCatching {
            val userInfo = firebaseDataDataSource.getUserInfo()?.toDomain()?.toLocal() ?: return
            userInfoDataSource.upsertUserInfo(userInfo)
        }.getOrElse { e -> e.printStackTrace() }
    }

    override fun observeUserInfo(): Flow<UserInfo?> {
        return userInfoDataSource.getUserInfoFlow().map { it?.toDomain() }
    }
}