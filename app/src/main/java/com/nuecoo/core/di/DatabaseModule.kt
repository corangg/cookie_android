package com.nuecoo.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nuecoo.core.data.database.NueCooDatabase
import com.nuecoo.core.data.database.dao.CookieDao
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
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS `LocalUserInfo` (
                `id` INTEGER NOT NULL,
                `email` TEXT NOT NULL,
                `nickname` TEXT NOT NULL,
                `phone` TEXT NOT NULL,
                `birth` TEXT NOT NULL,
                `gender` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """.trimIndent())
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            NueCooDatabase::class.java,
            "Database.db"
        ).addMigrations(MIGRATION_1_2).build()

    @Provides
    fun provideCookieDao(database: NueCooDatabase): CookieDao = database.cookieDao()

    @Provides
    fun provideUserInfoDao(database: NueCooDatabase): UserInfoDao = database.userInfoDao()
}