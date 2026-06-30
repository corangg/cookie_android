package com.nuecoo.feature.main.data.worker

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CookieSyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedule(eventId: String) {
        val request = OneTimeWorkRequestBuilder<CookieSyncWorker>()
            .setInputData(workDataOf("eventId" to eventId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "cookie_sync_$eventId",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}