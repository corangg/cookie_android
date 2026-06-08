import androidx.compose.ui.graphics.Color
import com.nuecoo.R
import com.nuecoo.core.ui.model.CommonDropDownItem
import com.nuecoo.feature.main.domain.model.CookieItemData
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.CookieTypeData
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.ui.theme.CheeringColor
import com.nuecoo.ui.theme.ComfortColor
import com.nuecoo.ui.theme.GrayText
import com.nuecoo.ui.theme.LoveColor
import com.nuecoo.ui.theme.PassionColor
import com.nuecoo.ui.theme.SermonColor
import com.nuecoo.ui.theme.UnknownColor

data class CookieTypeResource(
    val type: CookieType,
    val messageArrayRes: Int,
    val closedImgRes: Int,
    val openedImgRes: Int,
    val collectionImages: List<Int>,
    val mainTextRes: Int,
    val subTextRes: Int,
    val color: Color,
)

private val COOKIE_TYPE_RESOURCES = listOf(
    CookieTypeResource(
        type = CookieType.Cheering,
        messageArrayRes = R.array.cookie_type_cheering,
        closedImgRes = R.drawable.img_cookie_cheering_1,
        openedImgRes = R.drawable.img_cookie_cheering_6,
        collectionImages = listOf(
            R.drawable.img_cookie_cheering_1,
            R.drawable.img_cookie_cheering_2,
            R.drawable.img_cookie_cheering_3,
            R.drawable.img_cookie_cheering_4,
            R.drawable.img_cookie_cheering_5,
            R.drawable.img_cookie_cheering_6
        ),
        mainTextRes = R.string.text_open_cookie_cheer_main,
        subTextRes = R.string.text_open_cookie_cheer_sub,
        color = CheeringColor
    ),
    CookieTypeResource(
        type = CookieType.Comfort,
        messageArrayRes = R.array.cookie_type_consolation,
        closedImgRes = R.drawable.img_cookie_comfort_1,
        openedImgRes = R.drawable.img_cookie_comfort_6,
        collectionImages = listOf(
            R.drawable.img_cookie_comfort_1,
            R.drawable.img_cookie_comfort_2,
            R.drawable.img_cookie_comfort_3,
            R.drawable.img_cookie_comfort_4,
            R.drawable.img_cookie_comfort_5,
            R.drawable.img_cookie_comfort_6
        ),
        mainTextRes = R.string.text_open_cookie_comfort_main,
        subTextRes = R.string.text_open_cookie_comfort_sub,
        color = ComfortColor
    ),
    CookieTypeResource(
        type = CookieType.Passion,
        messageArrayRes = R.array.cookie_type_passion,
        closedImgRes = R.drawable.img_cookie_passion_1,
        openedImgRes = R.drawable.img_cookie_passion_6,
        collectionImages = listOf(
            R.drawable.img_cookie_passion_1,
            R.drawable.img_cookie_passion_2,
            R.drawable.img_cookie_passion_3,
            R.drawable.img_cookie_passion_4,
            R.drawable.img_cookie_passion_5,
            R.drawable.img_cookie_passion_6
        ),
        mainTextRes = R.string.text_open_cookie_passion_main,
        subTextRes = R.string.text_open_cookie_passion_sub,
        color = PassionColor
    ),
    CookieTypeResource(
        type = CookieType.Sermon,
        messageArrayRes = R.array.cookie_type_determination,
        closedImgRes = R.drawable.img_cookie_sermon_1,
        openedImgRes = R.drawable.img_cookie_sermon_6,
        collectionImages = listOf(
            R.drawable.img_cookie_sermon_1,
            R.drawable.img_cookie_sermon_2,
            R.drawable.img_cookie_sermon_3,
            R.drawable.img_cookie_sermon_4,
            R.drawable.img_cookie_sermon_5,
            R.drawable.img_cookie_sermon_6
        ),
        mainTextRes = R.string.text_open_cookie_sermon_main,
        subTextRes = R.string.text_open_cookie_sermon_sub,
        color = SermonColor
    ),
    CookieTypeResource(
        type = CookieType.Love,
        messageArrayRes = R.array.cookie_type_love,
        closedImgRes = R.drawable.img_cookie_love_1,
        openedImgRes = R.drawable.img_cookie_love_6,
        collectionImages = listOf(
            R.drawable.img_cookie_love_1,
            R.drawable.img_cookie_love_2,
            R.drawable.img_cookie_love_3,
            R.drawable.img_cookie_love_4,
            R.drawable.img_cookie_love_5,
            R.drawable.img_cookie_love_6
        ),
        mainTextRes = R.string.text_open_cookie_love_main,
        subTextRes = R.string.text_open_cookie_love_sub,
        color = LoveColor
    )
)

private fun getCookieTypeResource(type: Int?): CookieTypeResource? {
    return COOKIE_TYPE_RESOURCES.find { it.type.type == type }
}

fun CookieItemData.toUiItem(): CookieUIItemData {
    val resource = getCookieTypeResource(this.type)

    val img = when (this.isOpened) {
        null -> R.drawable.img_cookie_deactive
        true -> resource?.openedImgRes ?: R.drawable.img_cookie_deactive
        false -> resource?.closedImgRes ?: R.drawable.img_cookie_deactive
    }

    return CookieUIItemData(
        time = this.time,
        type = this.type,
        no = this.no,
        isOpened = this.isOpened,
        imgRes = img
    )
}

fun getCollectionTypeImages(type: Int?): List<Int> {
    return getCookieTypeResource(type)?.collectionImages
        ?: listOf(R.drawable.img_cookie_deactive)
}

fun getCookieTypeList(): List<CookieTypeData> {
    return COOKIE_TYPE_RESOURCES.map { resource ->
        CookieTypeData(
            type = resource.type,
            imgRes = resource.closedImgRes,
            nameRes = resource.mainTextRes
        )
    }
}

fun getCookieMessageArrayRes(type: Int): Int {
    return getCookieTypeResource(type)?.messageArrayRes ?: 0
}

fun getCookieMessageResMap(): Map<Int, Int> {
    return COOKIE_TYPE_RESOURCES.associate {
        it.type.type to it.messageArrayRes
    }
}

fun getCookieAnimationFrames(type: Int): List<Int> {
    return getCookieTypeResource(type)
        ?.collectionImages
        ?.drop(1)
        ?.dropLast(1)
        ?: listOf(R.drawable.img_cookie_deactive)
}

fun getOpenedCookieImage(type: Int): Int {
    return getCookieTypeResource(type)?.openedImgRes
        ?: R.drawable.img_cookie_deactive
}

fun getCookieTypeColor(type: Int?): Color {
    return getCookieTypeResource(type)?.color ?: UnknownColor
}

fun getCookieTypeMainTextRes(type: Int): Int {
    return getCookieTypeResource(type)?.mainTextRes
        ?: R.string.text_open_cookie_error_main
}

fun getCookieTypeSubTextRes(type: Int): Int {
    return getCookieTypeResource(type)?.subTextRes ?: 0
}