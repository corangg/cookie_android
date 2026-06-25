package com.nuecoo.core.data.datasource.remote

import com.nuecoo.core.data.model.remote.RemoteUserInfo

interface FirebaseDataDataSource {
    suspend fun saveUserInfo(uid: String, userInfo: RemoteUserInfo)
    suspend fun saveEmail(email: String)
    suspend fun checkEmailExists(email: String): Boolean
}