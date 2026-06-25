package com.nuecoo.feature.auth.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.nuecoo.core.data.datasource.remote.FirebaseDataDataSource
import com.nuecoo.core.data.mapper.toRTDBForm
import com.nuecoo.core.data.mapper.toRemote
import com.nuecoo.core.data.mapper.toUserInfo
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.feature.auth.data.datasource.FirebaseAuthDataSource
import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.SignUpResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @RemoteDataSources private val firebaseAuthDataSource: FirebaseAuthDataSource,
    @RemoteDataSources private val firebaseDataDataSource: FirebaseDataDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : AuthRepository {
    override suspend fun trySignUp(authModel: AuthModel) = withContext(ioDispatcher) {
        val userInfo = authModel.toUserInfo()
        val authResult = runCatching {
            firebaseAuthDataSource.trySignUp(authModel.email, authModel.password)
        }.getOrElse { e ->
            when (e) {
                is FirebaseAuthWeakPasswordException -> {
                    return@withContext SignUpResult.WeakPassword
                }

                is FirebaseAuthUserCollisionException -> {
                    return@withContext SignUpResult.AlreadyExists
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    return@withContext SignUpResult.InvalidEmail
                }

                else -> {
                    return@withContext SignUpResult.Failed
                }
            }
        }
        val uid = authResult.user?.uid ?: return@withContext SignUpResult.Failed
        runCatching {
            firebaseDataDataSource.saveUserInfo(uid, userInfo.toRemote())
            firebaseDataDataSource.saveEmail(authModel.email.toRTDBForm())
        }.getOrElse { return@withContext SignUpResult.DbSaveFailed }
        SignUpResult.Success
    }

    override suspend fun logOut(): Boolean = withContext(ioDispatcher) {
        runCatching {
            firebaseAuthDataSource.logOut()
            false
        }.getOrElse { return@withContext true }
    }



    /*override suspend fun isLoggedIn(): Boolean = firebaseAuthDataSourceImpl.isLoggedIn()

    override suspend fun login(email: String, password: String): Boolean =
        firebaseAuthDataSourceImpl.login(email, password)

    override suspend fun logout(): Boolean = firebaseAuthDataSourceImpl.logout()

    override suspend fun checkEmailExists(email: String): Boolean =
        firebaseAuthDataSourceImpl.checkEmailExists(email)

    override suspend fun sendVerificationCode(phoneNumber: String): String =
        firebaseAuthDataSourceImpl.sendVerificationCode(phoneNumber)

    override suspend fun verifySmsCode(verificationId: String, code: String): Boolean =
        firebaseAuthDataSourceImpl.verifySmsCode(verificationId, code)

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
            firebaseAuthDataSourceImpl.verifySmsCode(verificationId, smsCode)
        } else true
        if (!phoneVerified) return@runCatching false
        firebaseAuthDataSourceImpl.createAccount(email, password)
    }.getOrElse { false }*/
}