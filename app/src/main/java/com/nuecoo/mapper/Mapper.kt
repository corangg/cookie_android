import android.content.Context
import androidx.compose.ui.graphics.Color
import com.nuecoo.R
import com.nuecoo.feature.main.domain.model.CookieSlotUi
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.CookieTypeData
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.feature.main.domain.model.isSaved
import com.nuecoo.core.theme.CheeringBackgroundColor
import com.nuecoo.core.theme.CheeringColor
import com.nuecoo.core.theme.ComfortBackgroundColor
import com.nuecoo.core.theme.ComfortColor
import com.nuecoo.core.theme.LoveBackgroundColor
import com.nuecoo.core.theme.LoveColor
import com.nuecoo.core.theme.PassionBackgroundColor
import com.nuecoo.core.theme.PassionColor
import com.nuecoo.core.theme.SermonBackgroundColor
import com.nuecoo.core.theme.SermonColor
import com.nuecoo.core.theme.UnknownColor

data class CookieTypeResource(
    val type: CookieType,
    val messageArrayRes: Int,
    val closedImgRes: Int,
    val openedImgRes: Int,
    val collectionImages: List<Int>,
    val mainTextRes: Int,
    val subTextRes: Int,
    val color: Color,
    val backgroundColor: Color,
    val allCollectedTextRes: Int
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
        color = CheeringColor,
        backgroundColor = CheeringBackgroundColor,
        allCollectedTextRes = R.string.text_open_all_colleted_cheering
    ),
    CookieTypeResource(
        type = CookieType.Comfort,
        messageArrayRes = R.array.cookie_type_consolation,
        closedImgRes = R.drawable.img_cookie_comfort_1,
        openedImgRes = R.drawable.img_cookie_comfort_11,
        collectionImages = listOf(
            R.drawable.img_cookie_comfort_1,
            R.drawable.img_cookie_comfort_2,
            R.drawable.img_cookie_comfort_3,
            R.drawable.img_cookie_comfort_4,
            R.drawable.img_cookie_comfort_5,
            R.drawable.img_cookie_comfort_6,
            R.drawable.img_cookie_comfort_7,
            R.drawable.img_cookie_comfort_8,
            R.drawable.img_cookie_comfort_9,
            R.drawable.img_cookie_comfort_10,
            R.drawable.img_cookie_comfort_11
        ),
        mainTextRes = R.string.text_open_cookie_comfort_main,
        subTextRes = R.string.text_open_cookie_comfort_sub,
        color = ComfortColor,
        backgroundColor = ComfortBackgroundColor,
        allCollectedTextRes = R.string.text_open_all_colleted_comfort
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
        color = PassionColor,
        backgroundColor = PassionBackgroundColor,
        allCollectedTextRes = R.string.text_open_all_colleted_passion
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
        color = SermonColor,
        backgroundColor = SermonBackgroundColor,
        allCollectedTextRes = R.string.text_open_all_colleted_sermon
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
        color = LoveColor,
        backgroundColor = LoveBackgroundColor,
        allCollectedTextRes = R.string.text_open_all_colleted_love
    )
)

private fun getCookieTypeResource(type: Int?): CookieTypeResource? {
    return COOKIE_TYPE_RESOURCES.find { it.type.type == type }
}

fun CookieSlotUi.toUiItem(): CookieUIItemData {
    val resource = getCookieTypeResource(this.type)
    return when (this) {
        is CookieSlotUi.Empty -> CookieUIItemData(
            type = type,
            isFull = false,
            isOpened = false,
            no = null,
            imgRes = resource?.closedImgRes ?: R.drawable.img_cookie_deactive
        )
        is CookieSlotUi.InProgress -> CookieUIItemData(
            type = type,
            isFull = false,
            isOpened = false,
            no = null,
            imgRes = resource?.closedImgRes ?: R.drawable.img_cookie_deactive
        )
        is CookieSlotUi.Filled -> {
            val savedEvent = events.firstOrNull { it.isSaved }
            CookieUIItemData(
                type = type,
                isFull = false,
                isOpened = savedEvent != null,
                no = savedEvent?.cookieNo,
                imgRes = if (savedEvent != null) resource?.openedImgRes ?: R.drawable.img_cookie_deactive
                         else resource?.closedImgRes ?: R.drawable.img_cookie_deactive
            )
        }
    }
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

fun getCookieTypeBackgroundColor(type: Int?): Color {
    return getCookieTypeResource(type)?.backgroundColor ?: UnknownColor
}

fun getCookieTypeMainTextRes(type: Int): Int {
    return getCookieTypeResource(type)?.mainTextRes
        ?: R.string.text_open_cookie_error_main
}

fun getCookieTypeAllCollectedTextRes(type: Int): Int {
    return getCookieTypeResource(type)?.allCollectedTextRes
        ?: R.string.text_open_all_colleted_error
}

fun getCookieTypeSubTextRes(type: Int): Int {
    return getCookieTypeResource(type)?.subTextRes ?: R.string.text_open_cookie_error_main
}

fun Context.getCookieTypeListSize(): List<Pair<CookieType, Int>> {
    return COOKIE_TYPE_RESOURCES.map {
        Pair(it.type, resources.getStringArray(it.messageArrayRes).size)
    }
}
