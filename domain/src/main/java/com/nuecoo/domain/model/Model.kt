package com.nuecoo.domain.model

import androidx.annotation.DrawableRes


data class DailyCookieItemData(
    val date: String,
    val list: List<CookieItemData>
)
data class CookieItemData(
    val time: String? = null,
    val type: Int,
    val no: Int? = null,
    val isOpened: Boolean? = false,
)

data class CookieUIItemData(
    val time: String? = null,
    val type: Int,
    val no: Int? = null,
    val isOpened: Boolean? = false,
    @field:DrawableRes val imgRes: Int
)

data class CookieTypeData(
    val type: CookieType,
    @field:DrawableRes val imgRes: Int
)

enum class CookieType(val type: Int) {
    Cheering(0),
    Comfort(1),
    Passion(2),
    Sermon(3),
    Love(4),
    Unknown(-1)
}