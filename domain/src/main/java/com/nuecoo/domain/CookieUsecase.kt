package com.nuecoo.domain

import javax.inject.Inject

class ObserveDailyCookieData @Inject constructor(
    private val repository: LocalRepository
) {
    operator fun invoke() = repository.getDailyCookieData()
}