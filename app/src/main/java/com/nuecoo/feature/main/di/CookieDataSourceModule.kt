package com.nuecoo.feature.main.di

import android.content.Context
import androidx.work.WorkManager
import com.nuecoo.feature.main.data.datasource.local.CookieEventDataSource
import com.nuecoo.feature.main.data.datasource.local.CookieEventDataSourceImpl
import com.nuecoo.feature.main.data.datasource.local.CookieTypeCountLocalDataSource
import com.nuecoo.feature.main.data.datasource.local.CookieTypeCountLocalDataSourceImpl
import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.feature.main.data.datasource.remote.CookieRemoteDataSource
import com.nuecoo.feature.main.data.datasource.remote.CookieRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CookieDataSourceModule {
    @Binds
    @Singleton
    @RemoteDataSources
    abstract fun bindCookieRemoteDataSource(impl: CookieRemoteDataSourceImpl): CookieRemoteDataSource

    @Binds
    @Singleton
    @LocalDataSources
    abstract fun bindCookieEventDataSource(impl: CookieEventDataSourceImpl): CookieEventDataSource

    @Binds
    @Singleton
    @LocalDataSources
    abstract fun bindCookieTypeCountDataSource(impl: CookieTypeCountLocalDataSourceImpl): CookieTypeCountLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)
    }
}
