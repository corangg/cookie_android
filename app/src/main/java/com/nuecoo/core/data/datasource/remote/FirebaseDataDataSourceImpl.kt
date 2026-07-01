package com.nuecoo.core.data.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nuecoo.core.data.mapper.toRTDBForm
import com.nuecoo.core.data.mapper.toUserProfile
import com.nuecoo.core.data.model.remote.RemoteUserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import javax.inject.Inject

class FirebaseDataDataSourceImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : FirebaseDataDataSource {
    override suspend fun saveSignUpData(
        uid: String,
        userInfo: RemoteUserInfo,
        email: String,
        phone: String
    ) {
        val updates = hashMapOf(
            "users/$uid" to userInfo,
            "emails/${email.toRTDBForm()}" to true,
            "phoneToUid/${encodePhoneKey(phone)}" to uid
        )
        database.reference.updateChildren(updates).await()
    }

    private fun encodePhoneKey(phone: String): String =
        URLEncoder.encode(phone, "UTF-8")

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


