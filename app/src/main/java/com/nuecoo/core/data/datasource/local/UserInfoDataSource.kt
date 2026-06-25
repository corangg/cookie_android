package com.nuecoo.core.data.datasource.local

import com.nuecoo.core.data.model.local.LocalUserInfo
import kotlinx.coroutines.flow.Flow

interface UserInfoDataSource {
    suspend fun upsertUserInfo(entity: LocalUserInfo)
    suspend fun getUserInfo(): LocalUserInfo?
    suspend fun deleteUserInfo()
    fun getUserInfoFlow(): Flow<LocalUserInfo?>
}