package com.nuecoo.feature.auth.di

import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.feature.auth.data.datasource.FirebaseAuthDataSource
import com.nuecoo.feature.auth.data.datasource.FirebaseAuthDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRemoteDataSource {
    @Binds
    @Singleton
    @RemoteDataSources
    abstract fun bindRemoteAuthDataSource(impl: FirebaseAuthDataSourceImpl): FirebaseAuthDataSource

}