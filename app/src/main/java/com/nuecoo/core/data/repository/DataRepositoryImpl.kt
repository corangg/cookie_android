package com.nuecoo.core.data.repository

import com.nuecoo.core.data.datasource.remote.FirebaseDataDataSource
import com.nuecoo.core.data.mapper.toRTDBForm
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.RemoteDataSources
import com.nuecoo.core.domain.repository.DataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    @param:RemoteDataSources private val firebaseDataDataSource: FirebaseDataDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DataRepository {
    override suspend fun checkEmailExists(email: String): Boolean = withContext(ioDispatcher) {
        runCatching { firebaseDataDataSource.checkEmailExists(email.toRTDBForm()) }.getOrElse { false }
    }
}