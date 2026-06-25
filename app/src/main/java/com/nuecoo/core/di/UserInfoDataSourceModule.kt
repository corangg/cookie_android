package com.nuecoo.core.di

import com.nuecoo.core.data.datasource.local.UserInfoDataSource
import com.nuecoo.core.data.datasource.local.UserInfoDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserInfoDataSourceModule {
    @Binds
    @Singleton
    @LocalDataSources
    abstract fun bindLocalUserInfoDataSource(impl: UserInfoDataSourceImpl): UserInfoDataSource
}