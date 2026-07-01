package com.nuecoo.core.data.datasource.remote

import com.nuecoo.core.data.model.remote.RemoteUserInfo
import kotlinx.coroutines.flow.Flow

interface FirebaseDataDataSource {
    suspend fun saveSignUpData(uid: String, userInfo: RemoteUserInfo, email: String, phone: String)
    suspend fun checkEmailExists(email: String): Boolean

    suspend fun getUserInfo(): RemoteUserInfo?
}