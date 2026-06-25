package com.nuecoo.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalUserInfo(
    @PrimaryKey val id: Int = 1,
    val email: String,
    val nickname: String,
    val phone: String,
    val birth: String,
    val gender: Boolean,
)