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

fun CookieUIItemData.toOpenItem() : Int{
    val img = if (this.isOpened) {
        when (this.type) {
            CookieType.Cheering.type -> R.drawable.img_cookie_cheering_5
            CookieType.Consolation.type -> R.drawable.img_cookie_comfort_5
            CookieType.Passion.type -> R.drawable.img_cookie_passion_5
            CookieType.Determination.type -> R.drawable.img_cookie_sermon_5
            else -> R.drawable.img_cookie_deactive
        }
    } else {
        when (this.type) {
            CookieType.Cheering.type -> R.drawable.img_cookie_cheering_2
            CookieType.Consolation.type -> R.drawable.img_cookie_comfort_2
            CookieType.Passion.type -> R.drawable.img_cookie_passion_2
            CookieType.Determination.type -> R.drawable.img_cookie_sermon_2
            else -> R.drawable.img_cookie_deactive
        }
    }
    return img
}