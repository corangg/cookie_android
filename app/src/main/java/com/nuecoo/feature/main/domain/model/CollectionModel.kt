package com.nuecoo.feature.main.domain.model

import com.nuecoo.R

enum class CollectionSortType(
    val nameRes: Int
) {
    BY_NO(R.string.text_collection_no),
    BY_DATE(R.string.text_collection_date)
}

data class CollectionDisplayItem(
    val no: Int,
    val isCollected: Boolean,
    val type: Int,
    val date: String? = null,
    val message: String? = null,
)