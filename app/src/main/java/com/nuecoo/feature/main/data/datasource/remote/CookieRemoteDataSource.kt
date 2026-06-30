package com.nuecoo.feature.main.data.datasource.remote

import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.feature.main.domain.model.CookieSyncResult

interface CookieRemoteDataSource {
    suspend fun syncCookieEvent(eventId: String, type: Int, datetime: String): CookieSyncResult
    suspend fun getCookieTypeCounts(): Map<Int, Int>

    suspend fun fetchAllCookieEvents(): List<CookieEventEntity>
}