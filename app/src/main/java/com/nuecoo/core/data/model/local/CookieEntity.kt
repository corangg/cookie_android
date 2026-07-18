package com.nuecoo.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nuecoo.feature.main.domain.model.CookieSyncStatus

@Entity
data class CookieEventEntity(
    @PrimaryKey val eventId: String,
    val datetime: String,
    val claimDate: String,
    val type: Int,
    val message: String?,
    val cookieNo: Int? = null,
    val syncStatus: CookieSyncStatus = CookieSyncStatus.PENDING,
    val viaTicketGroupId: String? = null,
    val hasBeenViewed: Boolean = false
)

data class LocalTypeCollectedCount(
    val type: Int,
    val count: Int
)

@Entity
data class CookieTypeCountEntity(
    @PrimaryKey val type: Int,
    val maxCount: Int
)