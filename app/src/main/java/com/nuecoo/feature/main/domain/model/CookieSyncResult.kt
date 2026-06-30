package com.nuecoo.feature.main.domain.model

sealed interface CookieSyncResult {
    data class Saved(val cookieNo: Int, val message: String) : CookieSyncResult
    data class SavedViaTicket(val cookieNo: Int, val message: String, val ticketGroupId: String) : CookieSyncResult
    data class Rejected(val reason: RejectReason) : CookieSyncResult
    data class NetworkFailure(val cause: Throwable) : CookieSyncResult
}

enum class RejectReason {
    DAILY_LIMIT_AND_NO_TICKET,
    TICKET_EXHAUSTED_CONCURRENT,
    ALL_COLLECTED,
    UNKNOWN
}
