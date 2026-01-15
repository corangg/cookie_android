package com.nuecoo.domain

import androidx.annotation.DrawableRes

data class CookieItemData(
    val time: String,
    val type: Int,
    val isOpened: Boolean = false,
)

data class CookieUIItemData(
    val time: String,
    val type: Int,
    val isOpened: Boolean = false,
    @field:DrawableRes val imgRes: Int
)

enum class CookieType(val type: Int) {
    Cheering(0),
    Consolation(1),
    Passion(2),
    Determination(3),
}