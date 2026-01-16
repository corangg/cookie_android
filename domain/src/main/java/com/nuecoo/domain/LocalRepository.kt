package com.nuecoo.domain

import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun getDailyCookieData(): Flow<DailyCookieItemData>
}