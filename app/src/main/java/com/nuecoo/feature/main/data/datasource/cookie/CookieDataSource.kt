package com.nuecoo.feature.main.data.datasource.cookie

import com.nuecoo.core.database.entity.LocalDailyCookieData
import kotlinx.coroutines.flow.Flow

interface CookieDataSource {
    suspend fun upsertCookieData(entity: LocalDailyCookieData)
    suspend fun upsertCookieListData(entity: List<LocalDailyCookieData>)
    suspend fun getLastDailyCookieData(): LocalDailyCookieData?
    suspend fun getCookieList(): List<LocalDailyCookieData>
    fun observeLastDailyCookieData(): Flow<LocalDailyCookieData?>
    fun observeCookieList(): Flow<List<LocalDailyCookieData>>
    suspend fun deleteCookieData()
}