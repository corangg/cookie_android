package com.nuecoo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nuecoo.core.database.dao.CookieDao
import com.nuecoo.core.database.entity.LocalDailyCookieData

@Database(
    entities = [
        LocalDailyCookieData::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NueCooDatabase : RoomDatabase() {
    abstract fun cookieDao(): CookieDao
}