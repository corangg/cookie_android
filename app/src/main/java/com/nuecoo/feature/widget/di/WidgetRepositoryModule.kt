package com.nuecoo.feature.widget.di

import com.nuecoo.feature.widget.data.repository.WidgetRepositoryImpl
import com.nuecoo.feature.widget.domain.repository.WidgetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWidgetRepository(impl: WidgetRepositoryImpl): WidgetRepository
}
