package com.nuecoo.feature.main.data.repository

import com.nuecoo.core.data.mapper.toDomain
import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.core.data.model.local.CookieTypeCountEntity
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.core.util.nowAsYyyyMMddHHmm
import com.nuecoo.core.util.todayAsYyyyMMdd
import com.nuecoo.feature.main.data.datasource.local.CookieEventDataSource
import com.nuecoo.feature.main.data.datasource.local.CookieTypeCountLocalDataSource
import com.nuecoo.feature.main.data.datasource.remote.CookieRemoteDataSource
import com.nuecoo.feature.main.data.worker.CookieSyncScheduler
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.domain.model.TypeCollectedCount
import com.nuecoo.feature.main.domain.repository.CookieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class CookieRepositoryImpl @Inject constructor(
    @param:LocalDataSources private val cookieEventDataSource: CookieEventDataSource,
    @param:LocalDataSources private val cookieTypeCountLocalDataSource: CookieTypeCountLocalDataSource,
    @param:RemoteDataSources private val cookieRemoteDataSource: CookieRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val cookieSyncScheduler: CookieSyncScheduler,
) : CookieRepository {
    override suspend fun openCookie(type: Int) {
        val eventId = UUID.randomUUID().toString()
        insertCookieEvent(type, eventId)
        cookieSyncScheduler.schedule(eventId)
    }

    override fun observeEventsForToday() =
        cookieEventDataSource.observeEventsForToday(todayAsYyyyMMdd())
            .map { list ->
                list.map {
                    it.toDomain()
                }
            }

    override fun observeAllEvents() =
        cookieEventDataSource.observeAllEvents().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllEvents() =
        withContext(ioDispatcher) { cookieEventDataSource.getAllEvents().map { it.toDomain() } }

    override suspend fun refreshCounts() {
        runCatching {
            val counts = cookieRemoteDataSource.getCookieTypeCounts()
            val entities = counts.map { (type, maxCount) ->
                CookieTypeCountEntity(type = type, maxCount = maxCount)
            }
            cookieTypeCountLocalDataSource.upsertCookieTypeCount(entities)
        }
    }

    override fun observeCollectionProgress() =
        combine(
            cookieEventDataSource.observeDistinctCollectedCounts(),
            cookieTypeCountLocalDataSource.getCookieTypeCountFlow()
        ) { collectedList, allCounts ->
            collectedList.map {
                TypeCollectedCount(
                    type = it.type,
                    collectedCount = it.count,
                    maxCount = allCounts.find { count -> count.type == it.type }?.maxCount ?: 0
                )
            }
        }

    override suspend fun syncAllEventsFromServer() {
        runCatching {
            val events = cookieRemoteDataSource.fetchAllCookieEvents()
            cookieEventDataSource.deleteAll()
            cookieEventDataSource.insertAll(events)
        }
    }

    override suspend fun getCookieCount(type: Int) = cookieEventDataSource.getDistinctCollectedCount(type)

    override fun observeDailyClaimDates() = cookieEventDataSource.observeDailyClaimDates()

    private suspend fun insertCookieEvent(type: Int, eventId: String) {
        val now = nowAsYyyyMMddHHmm()
        cookieEventDataSource.insertCookieEvent(
            CookieEventEntity(
                eventId = eventId,
                datetime = now,
                claimDate = todayAsYyyyMMdd(),
                type = type,
                cookieNo = null,
                message = null,
                syncStatus = CookieSyncStatus.PENDING
            )
        )
    }

    override suspend fun getCookieCount() = withContext(ioDispatcher) {
        cookieTypeCountLocalDataSource.getCookieTypeCount().map {
            Pair(it.type, it.maxCount)
        }
    }
}