package com.nuecoo.core.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nuecoo.core.database.entity.LocalCookieData

class CookieConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromLocalCookieDataList(value: List<LocalCookieData>?): String {
        return gson.toJson(value ?: emptyList<LocalCookieData>())
    }

    @TypeConverter
    fun toLocalCookieDataList(value: String?): List<LocalCookieData> {
        if (value.isNullOrBlank()) return emptyList()

        val type = object : TypeToken<List<LocalCookieData>>() {}.type
        return gson.fromJson(value, type)
    }
}