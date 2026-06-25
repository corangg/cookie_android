package com.nuecoo.core.data.datasource.remote

import com.google.firebase.database.FirebaseDatabase
import com.nuecoo.core.data.model.remote.RemoteUserInfo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataDataSourceImpl @Inject constructor(
    private val database: FirebaseDatabase
) : FirebaseDataDataSource {
    override suspend fun saveUserInfo(uid: String, userInfo: RemoteUserInfo) {
        database.reference
            .child("users")
            .child(uid)
            .setValue(userInfo)
            .await()
    }
}


