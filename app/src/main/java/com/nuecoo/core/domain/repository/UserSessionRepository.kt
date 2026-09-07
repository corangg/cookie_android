package com.nuecoo.core.domain.repository

import com.nuecoo.core.domain.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {
    fun observeAuthState(): Flow<Boolean?>
    fun observeUserInfo(): Flow<UserInfo?>
    suspend fun refreshUserInfo()
    suspend fun logOut(): Boolean
    suspend fun withdraw(): Boolean
}
