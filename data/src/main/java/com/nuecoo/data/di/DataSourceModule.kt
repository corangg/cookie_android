package com.nuecoo.data.di

import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.data.datasource.LocalCookieDataSource
import com.nuecoo.data.datasource.local.CookieLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    @LocalDataSources
    abstract fun bindLocalCookieDataSource(impl: CookieLocalDataSource): LocalCookieDataSource
}