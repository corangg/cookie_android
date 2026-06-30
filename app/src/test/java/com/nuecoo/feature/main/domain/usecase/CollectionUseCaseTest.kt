package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.main.domain.model.CookieEvent
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.domain.repository.CookieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private fun savedEvent(type: Int, no: Int, date: String) =
    CookieEvent("id_${type}_${no}_$date", "${date}1000", date, type, no, CookieSyncStatus.SAVED)

// ──────────────────────────────────────────────
// GetCollectionByTypeUseCase
// ──────────────────────────────────────────────

class GetCollectionByTypeUseCaseTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: GetCollectionByTypeUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCollectionByTypeUseCase(repository)
    }

    @Test
    fun `저장된 데이터가 없으면 모든 항목이 미수집 상태이다`() = runTest {
        coEvery { repository.getAllEvents() } returns emptyList()

        val result = useCase(type = 0, totalSize = 3)

        assertEquals(3, result.size)
        assertTrue(result.all { !it.isCollected })
        assertEquals(listOf(1, 2, 3), result.map { it.no })
    }

    @Test
    fun `타입과 번호가 일치하는 항목만 수집 완료로 표시된다`() = runTest {
        coEvery { repository.getAllEvents() } returns listOf(
            savedEvent(0, 2, "20260620"),
            savedEvent(1, 1, "20260620")
        )

        val result = useCase(type = 0, totalSize = 3)

        assertFalse(result[0].isCollected) // no=1
        assertTrue(result[1].isCollected)  // no=2
        assertFalse(result[2].isCollected) // no=3
    }

    @Test
    fun `수집한 항목에는 날짜가 포함되고 미수집 항목은 날짜가 null이다`() = runTest {
        coEvery { repository.getAllEvents() } returns listOf(
            savedEvent(0, 1, "20260621")
        )

        val result = useCase(type = 0, totalSize = 2)

        assertEquals("20260621", result[0].date)
        assertNull(result[1].date)
    }

    @Test
    fun `같은 번호를 여러 날에 열어도 수집 횟수는 1로 계산된다`() = runTest {
        coEvery { repository.getAllEvents() } returns listOf(
            savedEvent(0, 1, "20260620"),
            savedEvent(0, 1, "20260621")
        )

        val result = useCase(type = 0, totalSize = 3)

        assertEquals(1, result.count { it.isCollected })
    }

    @Test
    fun `PENDING 상태 이벤트는 수집 완료로 보지 않는다`() = runTest {
        val pendingEvent = CookieEvent("id1", "202606201000", "20260620", 0, 1, CookieSyncStatus.PENDING)
        coEvery { repository.getAllEvents() } returns listOf(pendingEvent)

        val result = useCase(type = 0, totalSize = 2)

        assertTrue(result.none { it.isCollected })
    }
}

// ──────────────────────────────────────────────
// GetCollectionListUseCase
// ──────────────────────────────────────────────

class GetCollectionListUseCaseTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: GetCollectionListUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCollectionListUseCase(repository)
    }

    @Test
    fun `여러 타입의 사이즈 합만큼 항목이 반환된다`() = runTest {
        coEvery { repository.getAllEvents() } returns emptyList()

        val input = listOf(CookieType.Cheering to 3, CookieType.Comfort to 2)
        val result = useCase(input)

        assertEquals(5, result.size)
        assertEquals(3, result.count { it.type == CookieType.Cheering.type })
        assertEquals(2, result.count { it.type == CookieType.Comfort.type })
    }

    @Test
    fun `저장된 데이터가 없으면 모든 항목이 미수집 상태이다`() = runTest {
        coEvery { repository.getAllEvents() } returns emptyList()

        val result = useCase(listOf(CookieType.Cheering to 5))

        assertTrue(result.all { !it.isCollected })
    }

    @Test
    fun `타입별로 수집된 쿠키가 정확히 표시된다`() = runTest {
        coEvery { repository.getAllEvents() } returns listOf(
            savedEvent(CookieType.Cheering.type, 1, "20260620"),
            savedEvent(CookieType.Comfort.type, 2, "20260620")
        )

        val result = useCase(listOf(CookieType.Cheering to 3, CookieType.Comfort to 3))

        val cheeringNo1 = result.find { it.type == CookieType.Cheering.type && it.no == 1 }
        val comfortNo2 = result.find { it.type == CookieType.Comfort.type && it.no == 2 }
        assertTrue(cheeringNo1?.isCollected == true)
        assertTrue(comfortNo2?.isCollected == true)
    }

    @Test
    fun `같은 타입과 번호를 여러 날에 열어도 수집 횟수는 1로 계산된다`() = runTest {
        coEvery { repository.getAllEvents() } returns listOf(
            savedEvent(0, 1, "20260620"),
            savedEvent(0, 1, "20260621")
        )

        val result = useCase(listOf(CookieType.Cheering to 5))

        assertEquals(1, result.count { it.isCollected })
    }
}
