package com.nuecoo.core.data.database.converter

import androidx.room.TypeConverter
import com.nuecoo.core.domain.model.CookieSyncStatus

class SyncStatusConverter {
    @TypeConverter
    fun fromStatus(status: CookieSyncStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): CookieSyncStatus =
        runCatching { CookieSyncStatus.valueOf(value) }.getOrDefault(CookieSyncStatus.SYNC_FAILED)
}
