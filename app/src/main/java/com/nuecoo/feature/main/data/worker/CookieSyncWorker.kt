package com.nuecoo.feature.main.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.feature.main.data.datasource.local.CookieEventDataSource
import com.nuecoo.feature.main.data.datasource.remote.CookieRemoteDataSource
import com.nuecoo.feature.main.domain.model.CookieSyncResult
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.domain.model.RejectReason
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CookieSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    @param:RemoteDataSources private val remoteDataSource: CookieRemoteDataSource,
    @param:LocalDataSources private val localDataSource: CookieEventDataSource,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val eventId = inputData.getString("eventId") ?: return Result.failure()
        val event = localDataSource.getById(eventId) ?: return Result.success()

        return when (
            val result = remoteDataSource.syncCookieEvent(event.eventId, event.type, event.datetime)
        ) {
            is CookieSyncResult.Saved -> {
                localDataSource.updateStatus(
                    eventId = eventId,
                    status = CookieSyncStatus.SAVED,
                    cookieNo = result.cookieNo,
                    message = result.message
                )
                Result.success()
            }

            is CookieSyncResult.SavedViaTicket -> {
                localDataSource.updateStatus(
                    eventId = eventId,
                    status = CookieSyncStatus.SAVED_VIA_TICKET,
                    cookieNo = result.cookieNo,
                    message = result.message,
                    ticketGroupId = result.ticketGroupId
                )
                Result.success()
            }

            is CookieSyncResult.Rejected -> {
                val status = when (result.reason) {
                    RejectReason.DAILY_LIMIT_AND_NO_TICKET -> CookieSyncStatus.REJECTED_DAILY_LIMIT
                    RejectReason.TICKET_EXHAUSTED_CONCURRENT -> CookieSyncStatus.REJECTED_TICKET_RACE
                    RejectReason.ALL_COLLECTED -> CookieSyncStatus.REJECTED_ALL_COLLECTED
                    RejectReason.UNKNOWN -> CookieSyncStatus.SYNC_FAILED
                }
                localDataSource.updateStatus(eventId, status)
                Result.success()
            }

            is CookieSyncResult.NetworkFailure -> Result.retry()
        }
    }
}