package com.nuecoo.mapper

import com.nuecoo.R
import com.nuecoo.domain.CookieItemData
import com.nuecoo.domain.CookieType
import com.nuecoo.domain.CookieUIItemData

fun CookieItemData.toUiItem(): CookieUIItemData {
    val img = if (this.isOpened) {
        when (this.type) {
            CookieType.Cheering.type -> R.drawable.img_cookie_cheering_6
            CookieType.Consolation.type -> R.drawable.img_cookie_comfort_6
            CookieType.Passion.type -> R.drawable.img_cookie_passion_6
            CookieType.Determination.type -> R.drawable.img_cookie_sermon_6
            else -> R.drawable.img_cookie_deactive
        }
    } else {
        when (this.type) {
            CookieType.Cheering.type -> R.drawable.img_cookie_cheering_1
            CookieType.Consolation.type -> R.drawable.img_cookie_comfort_1
            CookieType.Passion.type -> R.drawable.img_cookie_passion_1
            CookieType.Determination.type -> R.drawable.img_cookie_sermon_1
            else -> R.drawable.img_cookie_deactive
        }
    }
    return CookieUIItemData(
        time = this.time,
        type = this.type,
        isOpened = this.isOpened,
        imgRes = img
    )
}