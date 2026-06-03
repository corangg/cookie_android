package com.nuecoo.mapper

import com.nuecoo.R
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.CookieType
import com.nuecoo.domain.model.CookieTypeData
import com.nuecoo.domain.model.CookieUIItemData

fun CookieItemData.toUiItem(): CookieUIItemData {
    val img = when (this.isOpened) {
        null -> R.drawable.img_cookie_deactive
        true -> when (this.type) {
            CookieType.Cheering.type -> R.drawable.img_cookie_cheering_6
            CookieType.Comfort.type -> R.drawable.img_cookie_comfort_6
            CookieType.Passion.type -> R.drawable.img_cookie_passion_6
            CookieType.Sermon.type -> R.drawable.img_cookie_sermon_6
            else -> R.drawable.img_cookie_deactive
        }
        false -> when (this.type) {
            CookieType.Cheering.type -> R.drawable.img_cookie_cheering_1
            CookieType.Comfort.type -> R.drawable.img_cookie_comfort_1
            CookieType.Passion.type -> R.drawable.img_cookie_passion_1
            CookieType.Sermon.type -> R.drawable.img_cookie_sermon_1
            else -> R.drawable.img_cookie_deactive
        }
    }
    return CookieUIItemData(time = this.time, type = this.type, no = this.no, isOpened = this.isOpened, imgRes = img)
}

fun getCollectionTypeImages(type: Int): List<Int> = when (type) {
    CookieType.Cheering.type -> listOf(
        R.drawable.img_cookie_cheering_1, R.drawable.img_cookie_cheering_2,
        R.drawable.img_cookie_cheering_3, R.drawable.img_cookie_cheering_4,
        R.drawable.img_cookie_cheering_5, R.drawable.img_cookie_cheering_6
    )
    CookieType.Comfort.type -> listOf(
        R.drawable.img_cookie_comfort_1, R.drawable.img_cookie_comfort_2,
        R.drawable.img_cookie_comfort_3, R.drawable.img_cookie_comfort_4,
        R.drawable.img_cookie_comfort_5, R.drawable.img_cookie_comfort_6
    )
    CookieType.Passion.type -> listOf(
        R.drawable.img_cookie_passion_1, R.drawable.img_cookie_passion_2,
        R.drawable.img_cookie_passion_3, R.drawable.img_cookie_passion_4,
        R.drawable.img_cookie_passion_5, R.drawable.img_cookie_passion_6
    )
    CookieType.Sermon.type -> listOf(
        R.drawable.img_cookie_sermon_1, R.drawable.img_cookie_sermon_2,
        R.drawable.img_cookie_sermon_3, R.drawable.img_cookie_sermon_4,
        R.drawable.img_cookie_sermon_5, R.drawable.img_cookie_sermon_6
    )
    else -> listOf(R.drawable.img_cookie_deactive)
}

fun getCookieTypeList(): List<CookieTypeData> {
    return CookieType.entries.filter { it != CookieType.Unknown }.map {
        CookieTypeData(
            type = it,
            imgRes = when (it) {
                CookieType.Cheering -> R.drawable.img_cookie_cheering_1
                CookieType.Comfort -> R.drawable.img_cookie_comfort_1
                CookieType.Passion -> R.drawable.img_cookie_passion_1
                CookieType.Sermon -> R.drawable.img_cookie_sermon_1
                else -> R.drawable.img_cookie_deactive
            }
        )
    }
}
