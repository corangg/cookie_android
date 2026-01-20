package com.nuecoo.domain.repository

import com.nuecoo.domain.model.DailyCookieItemData
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun getFlowDailyCookieData(): Flow<DailyCookieItemData?>

    fun getFlowCookieDataList(): Flow<List<DailyCookieItemData>>
    suspend fun getCookieDataList(): List<DailyCookieItemData>

    suspend fun upsertDailyCookieData(data: DailyCookieItemData): Boolean
}