package com.nuecoo.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoroutineModule {

    @IoDispatcher
    @Provides
    @Singleton
    fun provideIoDispatcher() = Dispatchers.IO

    @UnconfinedDispatcher
    @Provides
    @Singleton
    fun provideUnconfinedDispatcher() = Dispatchers.Default

    @DefaultDispatcher
    @Provides
    @Singleton
    fun providerDefaultDispatcher() = Dispatchers.Unconfined

    @MainDispatcher
    @Provides
    @Singleton
    fun provideMainDispatcher() = Dispatchers.Main
}
