package com.nuecoo.feature.main.data.datasource.remote

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.feature.main.domain.model.CookieSyncResult
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.domain.model.RejectReason
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CookieRemoteDataSourceImpl @Inject constructor(
    private val functions: FirebaseFunctions
) : CookieRemoteDataSource {

    override suspend fun syncCookieEvent(
        eventId: String,
        type: Int,
        datetime: String
    ): CookieSyncResult {
        return try {
            val result = functions
                .getHttpsCallable("syncCookieEvent")
                .call(mapOf("eventId" to eventId, "type" to type, "datetime" to datetime))
                .await()

            @Suppress("UNCHECKED_CAST")
            val data = result.getData() as Map<String, Any>
            val cookieNo = (data["cookieNo"] as Number).toInt()
            val message = data["message"] as String
            val viaTicket = data["viaTicket"] as? String

            if (viaTicket != null) CookieSyncResult.SavedViaTicket(cookieNo, message, viaTicket)
            else CookieSyncResult.Saved(cookieNo, message)
        } catch (e: FirebaseFunctionsException) {
            val reason = when (
                (e.details as? Map<*, *>)?.get("reason") as? String
            ) {
                "DAILY_LIMIT_AND_NO_TICKET" -> RejectReason.DAILY_LIMIT_AND_NO_TICKET
                "TICKET_EXHAUSTED_CONCURRENT" -> RejectReason.TICKET_EXHAUSTED_CONCURRENT
                "ALL_COLLECTED" -> RejectReason.ALL_COLLECTED
                else -> RejectReason.UNKNOWN
            }
            CookieSyncResult.Rejected(reason)
        } catch (e: Exception) {
            CookieSyncResult.NetworkFailure(e)
        }
    }

    override suspend fun getCookieTypeCounts(): Map<Int, Int> {
        val result = functions
            .getHttpsCallable("getCookieTypeCounts")
            .call()
            .await()
            .getData() as Map<*, *>

        val counts = result["counts"] as Map<*, *>

        return counts.entries.associate { (key, value) ->
            (key as String).toInt() to (value as Long).toInt()
        }
    }

    override suspend fun fetchAllCookieEvents(): List<CookieEventEntity> {
        val result = functions.getHttpsCallable("fetchCookieEvents").call().await().getData() as Map<*, *>
        val events = result["events"] as List<*>

        return events.map { raw ->
            val data = raw as Map<*, *>
            CookieEventEntity(
                eventId = data["eventId"] as String,
                datetime = data["datetime"] as String,
                claimDate = data["claimDate"] as String,
                type = (data["type"] as Number).toInt(),
                message = data["message"] as? String,
                cookieNo = (data["cookieNo"] as? Number)?.toInt(),
                syncStatus = if (data["viaTicket"] != null) CookieSyncStatus.SAVED_VIA_TICKET else CookieSyncStatus.SAVED,
                viaTicketGroupId = data["viaTicket"] as? String,
                hasBeenViewed = false
            )
        }
    }
}