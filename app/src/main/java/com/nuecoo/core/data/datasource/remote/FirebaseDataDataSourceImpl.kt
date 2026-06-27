package com.nuecoo.core.data.datasource.remote

import com.google.firebase.database.FirebaseDatabase
import com.nuecoo.core.data.mapper.toRTDBForm
import com.nuecoo.core.data.model.remote.RemoteUserInfo
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import javax.inject.Inject

class FirebaseDataDataSourceImpl @Inject constructor(
    private val database: FirebaseDatabase
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
}


