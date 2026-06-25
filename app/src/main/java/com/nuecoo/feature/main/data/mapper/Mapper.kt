package com.nuecoo.feature.main.data.mapper

import com.nuecoo.core.data.model.local.LocalCookieData
import com.nuecoo.core.data.model.local.LocalDailyCookieData
import com.nuecoo.feature.main.domain.model.CookieItemData
import com.nuecoo.feature.main.domain.model.DailyCookieItemData

fun DailyCookieItemData.toLocal() = LocalDailyCookieData(
    date = this.date,
    list = this.list.map { it.toLocal() }
)

fun CookieItemData.toLocal() = LocalCookieData(
    time = this.time,
    type = this.type,
    isFull = this.isFull,
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
    isFull = this.isFull,
    no = this.no,
    isOpened = this.isOpened
)