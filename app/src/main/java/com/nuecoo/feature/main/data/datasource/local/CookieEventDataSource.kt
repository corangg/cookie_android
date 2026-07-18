package com.nuecoo.feature.main.data.datasource.local

import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.core.data.model.local.LocalTypeCollectedCount
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import kotlinx.coroutines.flow.Flow

interface CookieEventDataSource {
    suspend fun insertCookieEvent(event: CookieEventEntity)
    fun observeEventsForToday(date: String): Flow<List<CookieEventEntity>>
    fun observeAllEvents(): Flow<List<CookieEventEntity>>
    suspend fun getAllEvents(): List<CookieEventEntity>
    suspend fun getById(eventId: String): CookieEventEntity?
    suspend fun updateStatus(
        eventId: String,
        status: CookieSyncStatus,
        cookieNo: Int? = null,
        message: String? = null,
        ticketGroupId: String? = null
    )
    suspend fun getDistinctCollectedCount(type: Int): Int
    fun observeDistinctCollectedCount(type: Int): Flow<Int>
    fun observeDistinctCollectedCounts(): Flow<List<LocalTypeCollectedCount>>
    suspend fun getAllByStatus(status: CookieSyncStatus): List<CookieEventEntity>
    suspend fun deleteAll()
    suspend fun insertAll(events: List<CookieEventEntity>)

    fun observeDailyClaimDates(): Flow<List<String>>
}