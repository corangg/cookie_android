package com.nuecoo.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * BaseViewModel은 ViewModel의 추상 클래스입니다.
 * 다양한 디스패처를 멤버로 가지며, 로딩 상태를 관리하는 기능을 제공합니다.
 *
 * @param mainDispatcher [MainCoroutineDispatcher] 메인 디스패처입니다.
 * @param defaultDispatcher [CoroutineDispatcher] 기본 디스패처입니다.
 * @param ioDispatcher [CoroutineDispatcher] IO 디스패처입니다.
 */
abstract class BaseViewModel(
    @MainDispatcher private val mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentJob: Job? = null

    fun cancelCurrentWork() {
        currentJob?.cancel()
        _isLoading.value = false
    }

    /**
     * 연산이 많이 필요한 작업을 실행하는데 사용됩니다.
     *
     * @param block [suspend CoroutineScope.() -> Unit] 작업을 수행하기 위한 블록 형태의 서스펜드 함수입니다.
     */
    protected fun onIntensiveWork(
        isLoading: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
    ) = onWork(isLoading, defaultDispatcher, block)

    /**
     * 가벼운 계산 작업을 실행하는데 사용됩니다.
     *
     * @param block [suspend CoroutineScope.() -> Unit] 작업을 수행하기 위한 블록 형태의 서스펜드 함수입니다.
     */
    protected fun onIoWork(isLoading: Boolean = true, block: suspend CoroutineScope.() -> Unit) =
        onWork(isLoading, ioDispatcher, block)

    /**
     * UI 작업을 실행하는데 사용됩니다.
     *
     * @param block [suspend CoroutineScope.() -> Unit] 작업을 수행하기 위한 블록 형태의 서스펜드 함수입니다.
     */
    protected fun onUiWork(isLoading: Boolean = true, block: suspend CoroutineScope.() -> Unit) =
        onWork(isLoading, mainDispatcher, block)

    /**
     * CoroutineDispatcher를 통해 서스펜드 함수를 실행하고, 그 동안 로딩 상태를 관리합니다.
     *
     * @param dispatcher [CoroutineDispatcher] 작업을 수행할 CoroutineDispatcher입니다.
     * @param block [suspend CoroutineScope.() -> Unit] 작업을 수행하기 위한 블록 형태의 서스펜드 함수입니다.
     */
    private fun onWork(
        isLoading: Boolean = true,
        dispatcher: CoroutineDispatcher,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(dispatcher) {
        if (isLoading) _isLoading.value = true
        try {
            block()
        } finally {
            if (isLoading) _isLoading.value = false
        }
    }.also { currentJob = it }
}
