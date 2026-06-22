package com.nuecoo.feature.widget.domain.usecase

import com.nuecoo.feature.widget.domain.repository.WidgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWidgetEnabledUseCase @Inject constructor(
    private val repository: WidgetRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.getFlowWidgetEnabled()
}

class SaveWidgetEnabledUseCase @Inject constructor(
    private val repository: WidgetRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.saveWidgetEnabled(enabled)
}
