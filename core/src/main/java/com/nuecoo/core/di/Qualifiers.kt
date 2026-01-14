package com.nuecoo.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UnconfinedDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalDataSources

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteDataSources