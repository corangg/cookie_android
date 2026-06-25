package com.nuecoo.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nuecoo.core.data.model.local.LocalUserInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Upsert
    suspend fun upsertUserInfo(entity: LocalUserInfo)

    @Query("SELECT * FROM LocalUserInfo WHERE id = 1")
    suspend fun getUserInfo(): LocalUserInfo?

    @Query("SELECT * FROM LocalUserInfo WHERE id = 1")
    fun getUserInfoFlow(): Flow<LocalUserInfo?>

    @Query("DELETE FROM LocalUserInfo")
    suspend fun deleteUser()
}