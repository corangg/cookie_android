package com.nuecoo.feature.main.domain.repository

import com.nuecoo.feature.main.domain.model.CookieEvent
import kotlinx.coroutines.flow.Flow

interface CookieRepository {
    suspend fun openCookie(type: Int)
    fun observeEventsForToday(): Flow<List<CookieEvent>>
    fun observeAllEvents(): Flow<List<CookieEvent>>
    suspend fun getAllEvents(): List<CookieEvent>
    suspend fun refreshCounts()
    suspend fun canOpenCookie(type: Int): Boolean
    fun observeCollectionProgress(type: Int): Flow<Pair<Int, Int?>>
    suspend fun syncAllEventsFromServer()

    fun observeDailyClaimDates(): Flow<List<String>>
}
