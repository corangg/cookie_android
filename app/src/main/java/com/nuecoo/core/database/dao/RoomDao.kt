package com.nuecoo.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nuecoo.core.database.entity.LocalDailyCookieData
import kotlinx.coroutines.flow.Flow

@Dao
interface CookieDao {
    @Upsert
    suspend fun upsertCookieData(entity: LocalDailyCookieData)

    @Upsert
    suspend fun upsertCookieListData(entity: List<LocalDailyCookieData>)

    @Query("SELECT * FROM LocalDailyCookieData ORDER BY date DESC LIMIT 1")
    suspend fun getLastDailyCookieData(): LocalDailyCookieData?

    @Query("SELECT * FROM LocalDailyCookieData")
    suspend fun getCookieList(): List<LocalDailyCookieData>

    @Query("SELECT * FROM LocalDailyCookieData ORDER BY date DESC LIMIT 1")
    fun observeLastDailyCookieData(): Flow<LocalDailyCookieData?>

    @Query("SELECT * FROM LocalDailyCookieData")
    fun observeCookieList(): Flow<List<LocalDailyCookieData>>

    @Query("DELETE FROM LocalDailyCookieData")
    suspend fun deleteCookieData()
}