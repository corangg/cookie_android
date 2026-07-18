package com.nuecoo.feature.main.data.datasource.local

import com.nuecoo.core.data.database.dao.CookieEventDao
import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CookieEventDataSourceImpl @Inject constructor(
    private val cookieEventDao: CookieEventDao
):CookieEventDataSource{
    override suspend fun insertCookieEvent(event: CookieEventEntity) = cookieEventDao.insert(event)
    override fun observeEventsForToday(date: String) = cookieEventDao.observeEventsForDate(date)
    override fun observeAllEvents() = cookieEventDao.observeAllEvents()
    override suspend fun getAllEvents() = cookieEventDao.getAllEvents()
    override suspend fun getById(eventId: String) = cookieEventDao.getById(eventId)
    override suspend fun updateStatus(
        eventId: String,
        status: CookieSyncStatus,
        cookieNo: Int?,
        message: String?,
        ticketGroupId: String?
    ) = cookieEventDao.updateStatus(eventId, status, cookieNo, ticketGroupId)
    override suspend fun getDistinctCollectedCount(type: Int) = cookieEventDao.getDistinctCollectedCount(type)
    override fun observeDistinctCollectedCount(type: Int) = cookieEventDao.observeDistinctCollectedCount(type)
    override fun observeDistinctCollectedCounts() = cookieEventDao.observeDistinctCollectedCounts()
    override suspend fun getAllByStatus(status: CookieSyncStatus) = cookieEventDao.getAllByStatus(status)
    override suspend fun deleteAll() = cookieEventDao.deleteAll()
    override suspend fun insertAll(events: List<CookieEventEntity>) = cookieEventDao.insertAll(events)
    override fun observeDailyClaimDates() = cookieEventDao.observeAllClaimDates()
}