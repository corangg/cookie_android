package com.nuecoo.data.di

import com.nuecoo.data.repository.DefaultLocalRepository
import com.nuecoo.domain.repository.LocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLocalRepository(impl: DefaultLocalRepository): LocalRepository
}