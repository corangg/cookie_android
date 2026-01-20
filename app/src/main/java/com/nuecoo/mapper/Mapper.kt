package com.nuecoo.mapper

import com.nuecoo.R
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.CookieType
import com.nuecoo.domain.model.CookieUIItemData

fun CookieItemData.toUiItem(): CookieUIItemData {
    val img = when (this.isOpened) {
        null -> {
            R.drawable.img_cookie_deactive
        }
        true -> {
            when (this.type) {
                CookieType.Cheering.type -> R.drawable.img_cookie_cheering_6
                CookieType.Consolation.type -> R.drawable.img_cookie_comfort_6
                CookieType.Passion.type -> R.drawable.img_cookie_passion_6
                CookieType.Determination.type -> R.drawable.img_cookie_sermon_6
                else -> R.drawable.img_cookie_deactive
            }
        }
        false -> {
            when (this.type) {
                CookieType.Cheering.type -> R.drawable.img_cookie_cheering_1
                CookieType.Consolation.type -> R.drawable.img_cookie_comfort_1
                CookieType.Passion.type -> R.drawable.img_cookie_passion_1
                CookieType.Determination.type -> R.drawable.img_cookie_sermon_1
                else -> R.drawable.img_cookie_deactive
            }
        }
    }

    return CookieUIItemData(
        time = this.time,
        type = this.type,
        isOpened = this.isOpened,
        imgRes = img
    )
}

fun CookieUIItemData.toOpenItem(): Int {
    val img = when (this.isOpened) {
        null -> {
            R.drawable.img_cookie_deactive
        }
        true -> {
            when (this.type) {
                CookieType.Cheering.type -> R.drawable.img_cookie_cheering_5
                CookieType.Consolation.type -> R.drawable.img_cookie_comfort_5
                CookieType.Passion.type -> R.drawable.img_cookie_passion_5
                CookieType.Determination.type -> R.drawable.img_cookie_sermon_5
                else -> R.drawable.img_cookie_deactive
            }
        }
        false -> {
            when (this.type) {
                CookieType.Cheering.type -> R.drawable.img_cookie_cheering_2
                CookieType.Consolation.type -> R.drawable.img_cookie_comfort_2
                CookieType.Passion.type -> R.drawable.img_cookie_passion_2
                CookieType.Determination.type -> R.drawable.img_cookie_sermon_2
                else -> R.drawable.img_cookie_deactive
            }
        }
    }
    return img
}

fun CookieUIItemData.toOpenAnimationItem(): List<Int> {
    return when (this.type) {
        CookieType.Cheering.type -> listOf(
            R.drawable.img_cookie_cheering_3,
            R.drawable.img_cookie_cheering_4,
            R.drawable.img_cookie_cheering_5
        )

        CookieType.Consolation.type -> listOf(
            R.drawable.img_cookie_comfort_3,
            R.drawable.img_cookie_comfort_4,
            R.drawable.img_cookie_comfort_5
        )

        CookieType.Passion.type -> listOf(
            R.drawable.img_cookie_passion_3,
            R.drawable.img_cookie_passion_4,
            R.drawable.img_cookie_passion_5
        )

        CookieType.Determination.type -> listOf(
            R.drawable.img_cookie_sermon_3,
            R.drawable.img_cookie_sermon_4,
            R.drawable.img_cookie_sermon_5
        )

        else -> listOf(R.drawable.img_cookie_deactive)
    }
}