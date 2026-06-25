package com.nuecoo.core.domain.repository

interface DataRepository {
    suspend fun checkEmailExists(email: String): Boolean
}