package com.nuecoo.core.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.nuecoo.core.data.model.local.LocalCookieData

class CookieConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromLocalCookieDataList(value: List<LocalCookieData>?): String {
        return gson.toJson(value ?: emptyList<LocalCookieData>())
    }

    @TypeConverter
    fun toLocalCookieDataList(value: String?): List<LocalCookieData> {
        if (value.isNullOrBlank()) return emptyList()

        val type = object : com.google.gson.reflect.TypeToken<List<LocalCookieData>>() {}.type
        return gson.fromJson(value, type)
    }
}