package com.nuecoo.core.data.mapper

import com.nuecoo.core.data.model.local.LocalUserInfo
import com.nuecoo.core.data.model.remote.RemoteUserInfo
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.UserInfo

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