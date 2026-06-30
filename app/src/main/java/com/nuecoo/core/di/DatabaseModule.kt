package com.nuecoo.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nuecoo.core.data.database.NueCooDatabase
import com.nuecoo.core.data.database.dao.CookieEventDao
import com.nuecoo.core.data.database.dao.CookieTypeCountDao
import com.nuecoo.core.data.database.dao.UserInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            NueCooDatabase::class.java,
            "Database.db"
        ).build()

    @Provides
    fun provideCookieEventDao(database: NueCooDatabase): CookieEventDao = database.cookieEventDao()
    @Provides
    fun provideCookieTypeCountDao(database: NueCooDatabase): CookieTypeCountDao = database.cookieTypeCountDao()
    @Provides
    fun provideUserInfoDao(database: NueCooDatabase): UserInfoDao = database.userInfoDao()
}
