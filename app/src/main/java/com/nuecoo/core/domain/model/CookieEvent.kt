package com.nuecoo.core.domain.model

data class CookieEvent(
    val eventId: String,
    val datetime: String,
    val claimDate: String,
    val message: String?,
    val type: Int,
    val cookieNo: Int? = null,
    val syncStatus: CookieSyncStatus = CookieSyncStatus.PENDING,
    val viaTicketGroupId: String? = null,
    val hasBeenViewed: Boolean = false
)

val CookieEvent.isSaved: Boolean
    get() = syncStatus == CookieSyncStatus.SAVED || syncStatus == CookieSyncStatus.SAVED_VIA_TICKET
