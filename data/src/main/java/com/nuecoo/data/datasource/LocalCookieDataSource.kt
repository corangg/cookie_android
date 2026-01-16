package com.nuecoo.data.datasource

import com.nuecoo.data.datasource.local.room.LocalDailyCookieData
import kotlinx.coroutines.flow.Flow

interface LocalCookieDataSource {
    suspend fun upsertCookieData(entity: LocalDailyCookieData)
    suspend fun upsertCookieListData(entity: List<LocalDailyCookieData>)
    suspend fun getLastDailyCookieData(): LocalDailyCookieData?
    suspend fun getCookieList(): List<LocalDailyCookieData>
    fun observeLastDailyCookieData(): Flow<LocalDailyCookieData>
    fun observeCookieList(): Flow<List<LocalDailyCookieData>>
    suspend fun deleteCookieData()
}