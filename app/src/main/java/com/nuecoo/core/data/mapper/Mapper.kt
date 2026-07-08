package com.nuecoo.core.data.mapper

import com.google.firebase.database.DataSnapshot
import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.core.data.model.local.LocalUserInfo
import com.nuecoo.core.data.model.remote.RemoteUserInfo
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.UserInfo
import com.nuecoo.feature.main.domain.model.CookieEvent

fun UserInfo.toRemote() = RemoteUserInfo(
    email = this.email,
    nickname = this.nickname,
    phone = this.phone,
    birth = this.birth,
    gender = this.gender,
)

fun UserInfo.toLocal() = LocalUserInfo(
    email = this.email,
    nickname = this.nickname,
    phone = this.phone,
    birth = this.birth,
    gender = this.gender,
)

fun RemoteUserInfo.toDomain() = UserInfo(
    email = this.email,
    nickname = this.nickname,
    phone = this.phone,
    birth = this.birth,
    gender = this.gender,
)

fun LocalUserInfo.toDomain() = UserInfo(
    email = this.email,
    nickname = this.nickname,
    phone = this.phone,
    birth = this.birth,
    gender = this.gender,
)

fun AuthModel.toUserInfo() = UserInfo(
    email = this.email,
    nickname = this.nickname,
    phone = this.phone,
    birth = this.birth,
    gender = this.gender,
)

fun String.toRTDBForm() = this.replace(".", ",")
fun String.toEmailForm() = this.replace(",", ".")

fun CookieEventEntity.toDomain() = CookieEvent(
    eventId = this.eventId,
    datetime = this.datetime,
    claimDate = this.claimDate,
    message = this.message,
    type = this.type,
    cookieNo = this.cookieNo,
    syncStatus = this.syncStatus,
    viaTicketGroupId = this.viaTicketGroupId,
    hasBeenViewed = this.hasBeenViewed
)

fun DataSnapshot.toUserProfile(): RemoteUserInfo? {
    return runCatching {
        RemoteUserInfo(
            email = child("email").getValue(String::class.java) ?: return null,
            nickname = child("nickname").getValue(String::class.java) ?: return null,
            phone = child("phone").getValue(String::class.java) ?: return null,
            birth = child("birth").getValue(String::class.java) ?: return null,
            gender = child("gender").getValue(Boolean::class.java) ?: return null
        )
    }.getOrNull()
}