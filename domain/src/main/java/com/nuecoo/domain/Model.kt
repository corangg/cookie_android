package com.nuecoo.domain

import androidx.annotation.DrawableRes


data class DailyCookieItemData(
    val date: String,
    val list: List<CookieItemData>
)
data class CookieItemData(
    val time: String? = null,
    val type: Int,
    val isOpened: Boolean = false,
)

data class CookieUIItemData(
    val time: String? = null,
    val type: Int,
    val isOpened: Boolean = false,
    @field:DrawableRes val imgRes: Int
)

enum class CookieType(val type: Int) {
    Cheering(0),
    Consolation(1),
    Passion(2),
    Determination(3),
    Unknown(-1)
}