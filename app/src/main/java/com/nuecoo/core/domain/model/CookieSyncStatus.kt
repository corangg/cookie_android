package com.nuecoo.core.domain.model

enum class CookieSyncStatus {
    PENDING, SAVED, SAVED_VIA_TICKET,
    REJECTED_DAILY_LIMIT, REJECTED_TICKET_RACE, SYNC_FAILED, REJECTED_ALL_COLLECTED
}
