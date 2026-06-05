package com.nuecoo.feature.main.di

import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.feature.main.data.datasource.cookie.CookieDataSource
import com.nuecoo.feature.main.data.datasource.cookie.CookieLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainDataSourceModule {
    @Binds
    @Singleton
    @LocalDataSources
    abstract fun bindLocalCookieDataSource(impl: CookieLocalDataSourceImpl): CookieDataSource
}