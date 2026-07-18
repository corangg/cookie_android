package com.nuecoo.feature.main.domain.repository

import com.nuecoo.feature.main.domain.model.CookieEvent
import com.nuecoo.feature.main.domain.model.TypeCollectedCount
import kotlinx.coroutines.flow.Flow

interface CookieRepository {
    suspend fun openCookie(type: Int)
    fun observeEventsForToday(): Flow<List<CookieEvent>>
    fun observeAllEvents(): Flow<List<CookieEvent>>
    suspend fun getAllEvents(): List<CookieEvent>
    suspend fun refreshCounts()
    fun observeCollectionProgress(): Flow<List<TypeCollectedCount>>
    suspend fun syncAllEventsFromServer()
    suspend fun getCookieCount(type: Int): Int
    fun observeDailyClaimDates(): Flow<List<String>>

    suspend fun getCookieCount(): List<Pair<Int, Int>>
}
