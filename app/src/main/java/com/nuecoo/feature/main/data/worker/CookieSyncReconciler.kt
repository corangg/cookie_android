package com.nuecoo.feature.main.data.worker

import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.feature.main.data.datasource.local.CookieEventDataSource
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import javax.inject.Inject

class CookieSyncReconciler @Inject constructor(
    @param:LocalDataSources private val cookieEventDataSource: CookieEventDataSource,
    private val scheduler: CookieSyncScheduler
) {
    suspend fun reconcilePendingEvents() {
        cookieEventDataSource.getAllByStatus(CookieSyncStatus.PENDING).forEach { event ->
            scheduler.schedule(event.eventId)
        }
    }
}