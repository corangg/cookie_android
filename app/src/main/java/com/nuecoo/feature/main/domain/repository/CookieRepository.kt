package com.nuecoo.feature.main.domain.repository

import com.nuecoo.feature.main.domain.model.DailyCookieItemData
import kotlinx.coroutines.flow.Flow

interface CookieRepository {
    fun getFlowDailyCookieData(): Flow<DailyCookieItemData?>

    fun getFlowCookieDataList(): Flow<List<DailyCookieItemData>>
    suspend fun getCookieDataList(): List<DailyCookieItemData>

    suspend fun upsertDailyCookieData(data: DailyCookieItemData): Boolean
}