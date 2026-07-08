package com.nuecoo.core.data.model.remote

data class RemoteUserInfo(
    val email: String,
    val nickname: String,
    val phone: String,
    val birth: String,
    val gender: Boolean,
)

data class RemoteAuthModel(
    val email: String,
    val password: String,
    val nickname: String,
    val phone: String,
    val birth: String,
    val gender: Boolean,
)
