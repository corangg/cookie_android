package com.nuecoo.data.di

import android.content.Context
import androidx.room.Room
import com.nuecoo.data.datasource.local.room.CookieDao
import com.nuecoo.data.datasource.local.room.Database
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
            com.nuecoo.data.datasource.local.room.Database::class.java,
            "Database.db"
        ).build()

    @Provides
    fun provideCookieDao(database: Database): CookieDao = database.cookieDao()
}