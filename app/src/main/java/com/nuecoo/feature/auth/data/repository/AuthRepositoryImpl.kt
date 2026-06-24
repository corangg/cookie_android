package com.nuecoo.feature.auth.data.repository

import android.content.Context
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.feature.auth.data.datasource.FirebaseAuthDataSource
import com.nuecoo.feature.auth.domain.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @RemoteDataSources private val firebaseAuthDataSource: FirebaseAuthDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : AuthRepository {

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