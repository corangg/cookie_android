package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.auth.domain.AuthRepository
import com.nuecoo.feature.main.data.worker.CookieSyncReconciler
import com.nuecoo.feature.main.domain.repository.CookieRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnLoginConfirmedUseCase @Inject constructor(
    private val userRepository: AuthRepository,
    private val cookieRepository: CookieRepository,
    private val cookieSyncReconciler: CookieSyncReconciler
) {
    suspend operator fun invoke() = coroutineScope {
        launch { userRepository.refreshUserInfo() }
        launch { cookieRepository.refreshCounts() }
        launch { cookieSyncReconciler.reconcilePendingEvents() }
        launch { cookieRepository.syncAllEventsFromServer() }
    }
}