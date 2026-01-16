package com.nuecoo.domain.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration
import javax.inject.Inject

class RemainTimeUseCase @Inject constructor(){
    operator fun invoke(): Flow<String> = flow {
        while (true){
            emit(getRemainingTimeText())
            delay(1_000)
        }
    }

    private fun getRemainingTimeToday(): Duration {
        val now = java.time.LocalDateTime.now()
        val endOfDay = now.toLocalDate().plusDays(1).atStartOfDay()
        return java.time.Duration.between(now, endOfDay)
    }

    private fun getRemainingTimeText(): String {
        val duration = getRemainingTimeToday()

        val hours = duration.toHours()
        val minutes = (duration.toMinutes() % 60)
        val seconds = (duration.seconds % 60)

        return String.format("%02d : %02d : %02d", hours, minutes, seconds)
    }
}