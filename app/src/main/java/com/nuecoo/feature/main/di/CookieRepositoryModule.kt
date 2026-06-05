package com.nuecoo.feature.main.di

import com.nuecoo.feature.main.data.repository.CookieRepositoryImpl
import com.nuecoo.feature.main.domain.repository.CookieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CookieRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCookieRepository(impl: CookieRepositoryImpl): CookieRepository
}
