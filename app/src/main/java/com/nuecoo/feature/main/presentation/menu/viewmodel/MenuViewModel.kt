package com.nuecoo.feature.main.presentation.menu.viewmodel

import androidx.lifecycle.viewModelScope
import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.main.domain.usecase.CheckTodayAttendanceUseCase
import com.nuecoo.feature.main.domain.usecase.GetAttendanceCount
import com.nuecoo.feature.main.domain.usecase.GetAttendanceDatesUseCase
import com.nuecoo.feature.main.domain.usecase.GetCollectionByTypeUseCase
import com.nuecoo.feature.main.domain.usecase.GetWeeklyAttendanceUseCase
import com.nuecoo.feature.main.domain.usecase.LogOutUseCase
import com.nuecoo.feature.main.domain.usecase.ObserveCollectionProgressUseCase
import com.nuecoo.feature.main.domain.usecase.ObserveUserInfoUseCase
import com.nuecoo.feature.main.domain.usecase.RefreshUserInfoUseCase
import com.nuecoo.feature.widget.domain.usecase.GetWidgetEnabledUseCase
import com.nuecoo.feature.widget.domain.usecase.SaveWidgetEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getCollectionByTypeUseCase: GetCollectionByTypeUseCase,
    private val logoutUseCase: LogOutUseCase,
    private val saveWidgetEnabledUseCase: SaveWidgetEnabledUseCase,
    private val refreshUserInfoUseCase: RefreshUserInfoUseCase,
    observeUserInfoUseCase: ObserveUserInfoUseCase,
    getAttendanceCount: GetAttendanceCount,
    checkTodayAttendanceUseCase: CheckTodayAttendanceUseCase,
    getWeeklyAttendanceUseCase: GetWeeklyAttendanceUseCase,
    getAttendanceDatesUseCase: GetAttendanceDatesUseCase,
    getWidgetEnabledUseCase: GetWidgetEnabledUseCase,
    observeCollectionProgressUseCase: ObserveCollectionProgressUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {
    val nickname = observeUserInfoUseCase().map { it?.nickname }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val collectionProgress = observeCollectionProgressUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val attendanceCount = getAttendanceCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val isTodayAttendance = checkTodayAttendanceUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val weeklyAttendance = getWeeklyAttendanceUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val attendanceDates = getAttendanceDatesUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
    val widgetEnabled = getWidgetEnabledUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun saveWidgetEnabled(enabled: Boolean) = onIoWork {
        saveWidgetEnabledUseCase(enabled)
    }

    fun logout() = onIoWork {
        _isLoggedIn.value = logoutUseCase()
    }

    fun refreshUserInfo() = onIoWork {
        refreshUserInfoUseCase()
    }
}
