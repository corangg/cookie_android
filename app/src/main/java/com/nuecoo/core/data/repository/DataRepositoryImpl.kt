package com.nuecoo.core.data.repository

import android.content.Context
import com.nuecoo.core.data.datasource.remote.FirebaseDataDataSource
import com.nuecoo.core.data.mapper.toRTDBForm
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.core.domain.repository.DataRepository
import com.nuecoo.feature.auth.data.datasource.FirebaseAuthDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    @RemoteDataSources private val firebaseAuthDataSource: FirebaseAuthDataSource,
    @RemoteDataSources private val firebaseDataDataSource: FirebaseDataDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : DataRepository {
    override suspend fun checkEmailExists(email: String): Boolean = withContext(ioDispatcher) {
        runCatching { firebaseDataDataSource.checkEmailExists(email.toRTDBForm()) }.getOrElse { false }
    }
}