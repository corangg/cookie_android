package com.nuecoo.feature.main.domain.model

import androidx.annotation.DrawableRes
import com.nuecoo.core.domain.model.CookieEvent
import com.nuecoo.core.domain.model.CookieSyncStatus

sealed interface CookieSlotUi {
    val type: Int

    data class Empty(override val type: Int) : CookieSlotUi
    data class InProgress(override val type: Int, val events: List<CookieEvent>) : CookieSlotUi
    data class Filled(override val type: Int, val events: List<CookieEvent>) : CookieSlotUi
}

val ALL_COOKIE_TYPES = listOf(0, 1, 2, 3, 4)

fun buildDailyCookieView(events: List<CookieEvent>): List<CookieSlotUi> {
    val byType = events.groupBy { it.type }
    return ALL_COOKIE_TYPES.map { type ->
        val typeEvents = byType[type].orEmpty()
        when {
            typeEvents.isEmpty() -> CookieSlotUi.Empty(type)
            typeEvents.any { it.syncStatus == CookieSyncStatus.PENDING } ->
                CookieSlotUi.InProgress(type, typeEvents)

            else -> CookieSlotUi.Filled(type, typeEvents)
        }
    }
}

data class CookieUIItemData(
    val time: String? = null,
    val type: Int,
    val isFull: Boolean = false,
    val no: Int? = null,
    val message: String?,
    val isOpened: Boolean? = false,
    @field:DrawableRes val imgRes: Int
)

data class CookieTypeData(
    val type: CookieType,
    val nameRes: Int,
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

data class TypeCollectedCount(
    val type: Int,
    val collectedCount: Int,
    val maxCount: Int
)