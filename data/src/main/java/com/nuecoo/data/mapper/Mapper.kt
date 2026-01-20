package com.nuecoo.data.mapper

import com.nuecoo.data.datasource.local.room.LocalCookieData
import com.nuecoo.data.datasource.local.room.LocalDailyCookieData
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.DailyCookieItemData

fun DailyCookieItemData.toLocal() = LocalDailyCookieData(
    date = this.date,
    list = this.list.map { it.toLocal() }
)

fun CookieItemData.toLocal() = LocalCookieData(
    time = this.time,
    type = this.type,
    no = this.no,
    isOpened = this.isOpened
)

fun LocalDailyCookieData.toExternal() = DailyCookieItemData(
    date = this.date,
    list = this.list.map { it.toExternal() }
)

fun LocalCookieData.toExternal() = CookieItemData(
    time = this.time,
    type = this.type,
    no = this.no,
    isOpened = this.isOpened
)