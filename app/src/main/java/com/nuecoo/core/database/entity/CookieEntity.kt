package com.nuecoo.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nuecoo.core.database.converter.CookieConverter

@Entity
@TypeConverters(CookieConverter::class)
data class LocalDailyCookieData(
    @PrimaryKey val date: String,
    val list: List<LocalCookieData>
)

data class LocalCookieData(
    val time: String? = null,
    val type: Int,
    val isFull: Boolean = false,
    val no: Int? = null,
    val isOpened: Boolean? = false,
)