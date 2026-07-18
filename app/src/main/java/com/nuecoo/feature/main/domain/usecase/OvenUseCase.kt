package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.main.domain.model.CookieSlotUi
import com.nuecoo.feature.main.domain.model.buildDailyCookieView
import com.nuecoo.feature.main.domain.repository.CookieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveDailyCookieData @Inject constructor(
    private val cookieRepository: CookieRepository
) {
    operator fun invoke(): Flow<List<CookieSlotUi>> =
        cookieRepository.observeEventsForToday().map { events -> buildDailyCookieView(events) }
}

class ObserveNotOpenedCookies @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<Int> =
        repository.observeEventsForToday().map { events ->
            buildDailyCookieView(events).count { it is CookieSlotUi.Empty }
        }
}

class OpenCookieUseCase @Inject constructor(
    private val repository: CookieRepository
){
    suspend operator fun invoke(type: Int) = repository.openCookie(type)
}

class GetCookieTotalCountsUseCase @Inject constructor(
    private val repository: CookieRepository
){
    suspend operator fun invoke(type: Int) = repository.getCookieCount(type)
}