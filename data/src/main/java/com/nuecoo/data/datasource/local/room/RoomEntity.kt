package com.nuecoo.data.datasource.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nuecoo.data.datasource.local.room.converter.CookieConverter

@Entity
@TypeConverters(CookieConverter::class)
data class LocalDailyCookieData(
    @PrimaryKey val date: String,
    val list: List<LocalCookieData>
)

data class LocalCookieData(
    val time: String? = null,
    val type: Int,
    val isOpened: Boolean = false,
)