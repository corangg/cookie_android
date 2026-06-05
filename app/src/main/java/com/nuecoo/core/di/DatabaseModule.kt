package com.nuecoo.core.di

import android.content.Context
import androidx.room.Room
import com.nuecoo.core.database.NueCooDatabase
import com.nuecoo.core.database.dao.CookieDao
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
    fun provideCookieDao(database: NueCooDatabase): CookieDao = database.cookieDao()
}