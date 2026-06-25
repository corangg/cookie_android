package com.nuecoo.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nuecoo.core.data.database.dao.CookieDao
import com.nuecoo.core.data.database.dao.UserInfoDao
import com.nuecoo.core.data.model.local.LocalDailyCookieData
import com.nuecoo.core.data.model.local.LocalUserInfo

@Database(
    entities = [
        LocalDailyCookieData::class,
        LocalUserInfo::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NueCooDatabase : RoomDatabase() {
    abstract fun cookieDao(): CookieDao
    abstract fun userInfoDao(): UserInfoDao
}