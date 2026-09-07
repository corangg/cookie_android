package com.nuecoo.core.domain.usecase

import com.nuecoo.core.domain.repository.WidgetSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWidgetEnabledUseCase @Inject constructor(
    private val repository: WidgetSettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.getFlowWidgetEnabled()
}

class SaveWidgetEnabledUseCase @Inject constructor(
    private val repository: WidgetSettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.saveWidgetEnabled(enabled)
}
