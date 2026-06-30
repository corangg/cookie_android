package com.nuecoo.core.di

import com.nuecoo.core.data.datasource.remote.FirebaseDataDataSource
import com.nuecoo.core.data.datasource.remote.FirebaseDataDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataRemoteDataSourceModule {
    @Binds
    @Singleton
    @RemoteDataSources
    abstract fun bindRemoteDataDataSource(impl: FirebaseDataDataSourceImpl): FirebaseDataDataSource
}