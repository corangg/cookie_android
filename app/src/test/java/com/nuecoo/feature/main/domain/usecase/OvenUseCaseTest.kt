package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.main.domain.model.ALL_COOKIE_TYPES
import com.nuecoo.feature.main.domain.model.CookieEvent
import com.nuecoo.feature.main.domain.model.CookieSlotUi
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.domain.repository.CookieRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private const val TODAY = "20260626"

// ──────────────────────────────────────────────
// ObserveNotOpenedCookies
// 아직 열리지 않은 쿠키 슬롯 개수를 관찰하는 UseCase 테스트
// ──────────────────────────────────────────────

class ObserveNotOpenedCookiesTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: ObserveNotOpenedCookies

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObserveNotOpenedCookies(repository)
    }

    @Test
    fun `이벤트가 없으면 모든 슬롯이 Empty이므로 전체 타입 수를 반환한다`() = runTest {
        every { repository.observeEventsForToday() } returns flowOf(emptyList())
        assertEquals(ALL_COOKIE_TYPES.size, useCase().first())
    }

    @Test
    fun `SAVED 이벤트가 있는 타입은 Filled이므로 카운트에서 제외된다`() = runTest {
        val savedEvent = CookieEvent("id1", "202606261000", TODAY, 0, 1, CookieSyncStatus.SAVED)
        every { repository.observeEventsForToday() } returns flowOf(listOf(savedEvent))
        // type 0은 Filled → Empty는 나머지 4개
        assertEquals(ALL_COOKIE_TYPES.size - 1, useCase().first())
    }

    @Test
    fun `PENDING 이벤트가 있는 타입은 InProgress이므로 Empty가 아니다`() = runTest {
        val pendingEvent = CookieEvent("id1", "202606261000", TODAY, 0, null, CookieSyncStatus.PENDING)
        every { repository.observeEventsForToday() } returns flowOf(listOf(pendingEvent))
        // type 0은 InProgress → Empty는 나머지 4개
        assertEquals(ALL_COOKIE_TYPES.size - 1, useCase().first())
    }
}

// ──────────────────────────────────────────────
// ObserveDailyCookieData
// 오늘의 CookieSlotUi 목록을 관찰하는 UseCase 테스트
// ──────────────────────────────────────────────

class ObserveDailyCookieDataTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: ObserveDailyCookieData

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObserveDailyCookieData(repository)
    }

    @Test
    fun `이벤트가 없으면 모든 슬롯이 Empty이다`() = runTest {
        every { repository.observeEventsForToday() } returns flowOf(emptyList())
        val result = useCase().first()
        assertEquals(ALL_COOKIE_TYPES.size, result.size)
        assert(result.all { it is CookieSlotUi.Empty })
    }

    @Test
    fun `PENDING 이벤트가 있는 타입은 InProgress 슬롯이다`() = runTest {
        val event = CookieEvent("id1", "202606261000", TODAY, 0, null, CookieSyncStatus.PENDING)
        every { repository.observeEventsForToday() } returns flowOf(listOf(event))
        val result = useCase().first()
        assert(result.find { it.type == 0 } is CookieSlotUi.InProgress)
    }

    @Test
    fun `SAVED 이벤트가 있는 타입은 Filled 슬롯이다`() = runTest {
        val event = CookieEvent("id1", "202606261000", TODAY, 0, 1, CookieSyncStatus.SAVED)
        every { repository.observeEventsForToday() } returns flowOf(listOf(event))
        val result = useCase().first()
        assert(result.find { it.type == 0 } is CookieSlotUi.Filled)
    }
}
