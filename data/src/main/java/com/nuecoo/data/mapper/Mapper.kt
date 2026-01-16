package com.nuecoo.data.mapper

import com.nuecoo.data.datasource.local.room.LocalCookieData
import com.nuecoo.data.datasource.local.room.LocalDailyCookieData
import com.nuecoo.domain.CookieItemData
import com.nuecoo.domain.DailyCookieItemData

fun DailyCookieItemData.toLocal() = LocalDailyCookieData(
    date = this.date,
    list = this.list.map { it.toLocal() }
)

fun CookieItemData.toLocal() = LocalCookieData(
    time = this.time,
    type = this.type,
    isOpened = this.isOpened
)

fun LocalDailyCookieData.toExternal() = DailyCookieItemData(
    date = this.date,
    list = this.list.map { it.toExternal() }
)

fun LocalCookieData.toExternal() = CookieItemData(
    time = this.time,
    type = this.type,
    isOpened = this.isOpened
)