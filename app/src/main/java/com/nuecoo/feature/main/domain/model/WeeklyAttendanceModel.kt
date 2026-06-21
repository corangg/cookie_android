package com.nuecoo.feature.main.domain.model

data class WeeklyAttendanceModel(
    val dayIndex: Int,
    val isAttendance: Boolean = false
)