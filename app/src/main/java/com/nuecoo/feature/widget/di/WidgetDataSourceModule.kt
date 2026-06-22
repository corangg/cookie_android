package com.nuecoo.feature.widget.di

import com.nuecoo.feature.widget.data.datasource.WidgetDataSource
import com.nuecoo.feature.widget.data.datasource.WidgetLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindWidgetLocalDataSource(impl: WidgetLocalDataSourceImpl): WidgetDataSource
}
