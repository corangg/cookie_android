package com.nuecoo.core.data.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nuecoo.core.data.mapper.toUserProfile
import com.nuecoo.core.data.model.remote.RemoteUserInfo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataDataSourceImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : FirebaseDataDataSource {
    override suspend fun checkEmailExists(email: String): Boolean {
        val snapshot = database.reference
            .child("emails")
            .child(email)
            .get()
            .await()
        snapshot.exists()
        return snapshot.exists()
    }

    override suspend fun getUserInfo(): RemoteUserInfo? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = database.getReference("users/$uid").get().await()
        return snapshot.toUserProfile()
    }
}


