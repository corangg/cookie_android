package com.nuecoo.domain.usecase

import com.nuecoo.domain.LocalRepository
import javax.inject.Inject

class ObserveDailyCookieData @Inject constructor(
    private val repository: LocalRepository
) {
    operator fun invoke() = repository.getDailyCookieData()
}