package com.nuecoo.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nuecoo.core.data.database.converter.SyncStatusConverter
import com.nuecoo.core.data.database.dao.CookieEventDao
import com.nuecoo.core.data.database.dao.CookieTypeCountDao
import com.nuecoo.core.data.database.dao.UserInfoDao
import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.core.data.model.local.CookieTypeCountEntity
import com.nuecoo.core.data.model.local.LocalUserInfo

@Database(
    entities = [
        CookieEventEntity::class,
        CookieTypeCountEntity::class,
        LocalUserInfo::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(SyncStatusConverter::class)
abstract class NueCooDatabase : RoomDatabase() {
    abstract fun cookieEventDao(): CookieEventDao
    abstract fun cookieTypeCountDao(): CookieTypeCountDao
    abstract fun userInfoDao(): UserInfoDao
}
