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
        return runCatching {
            val result = functions
                .getHttpsCallable("getCookieTypeCounts")
                .call()
                .await()

            val data = result.getData() as? Map<*, *> ?: return emptyMap()
            val counts = data["counts"] as? Map<*, *> ?: return emptyMap()

            counts.entries.mapNotNull { (key, value) ->
                val type = (key as? String)?.toIntOrNull() ?: return@mapNotNull null
                val count = (value as? Number)?.toInt() ?: return@mapNotNull null
                type to count
            }.toMap()
        }.getOrElse { emptyMap() }
    }

    override suspend fun fetchAllCookieEvents(): List<CookieEventEntity> {
        return runCatching {
            val result = functions
                .getHttpsCallable("fetchCookieEvents")
                .call()
                .await()

            val data = result.getData() as? Map<*, *> ?: return emptyList()
            val events = data["events"] as? List<*> ?: return emptyList()

            events.mapNotNull { raw ->
                val event = raw as? Map<*, *> ?: return@mapNotNull null
                runCatching {
                    CookieEventEntity(
                        eventId = event["eventId"] as? String ?: return@mapNotNull null,
                        datetime = event["datetime"] as? String ?: return@mapNotNull null,
                        claimDate = event["claimDate"] as? String ?: return@mapNotNull null,
                        type = (event["type"] as? Number)?.toInt() ?: return@mapNotNull null,
                        message = event["message"] as? String,
                        cookieNo = (event["cookieNo"] as? Number)?.toInt(),
                        syncStatus = if (event["viaTicket"] != null)
                            CookieSyncStatus.SAVED_VIA_TICKET else CookieSyncStatus.SAVED,
                        viaTicketGroupId = event["viaTicket"] as? String,
                        hasBeenViewed = false
                    )
                }.getOrNull()
            }
        }.getOrElse { emptyList() }
    }
}