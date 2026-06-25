package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.main.domain.model.CookieItemData
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
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

// ──────────────────────────────────────────────
// GetCollectionByTypeUseCase
// 특정 타입의 쿠키 컬렉션 목록을 반환하는 UseCase 테스트
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
        // 레포지토리가 빈 리스트를 반환할 때 totalSize 만큼의 미수집 항목이 생성되어야 함
        coEvery { repository.getCookieDataList() } returns emptyList()

        val result = useCase(type = 0, totalSize = 3)

        assertEquals(3, result.size)
        assertTrue(result.all { !it.isCollected })
        // 번호는 1부터 totalSize까지 순서대로 채워짐
        assertEquals(listOf(1, 2, 3), result.map { it.no })
    }

    @Test
    fun `타입과 번호가 일치하는 항목만 수집 완료로 표시된다`() = runTest {
        // type=0, no=2 가 열린 상태 / type=1 은 다른 타입이므로 무시
        val data = listOf(
            DailyCookieItemData(
                date = "20260620",
                list = listOf(
                    CookieItemData(type = 0, no = 2, isOpened = true),
                    CookieItemData(type = 1, no = 1, isOpened = true), // 다른 타입 → 무시
                )
            )
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(type = 0, totalSize = 3)

        assertFalse(result[0].isCollected) // no=1 미수집
        assertTrue(result[1].isCollected)  // no=2 수집 완료
        assertFalse(result[2].isCollected) // no=3 미수집
    }

    @Test
    fun `수집한 항목에는 열린 날짜가 포함되고 미수집 항목은 날짜가 null이다`() = runTest {
        // 수집 완료 항목은 date 필드에 열린 날짜가 기록되어야 함
        val data = listOf(
            DailyCookieItemData(
                date = "20260621",
                list = listOf(CookieItemData(type = 0, no = 1, isOpened = true))
            )
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(type = 0, totalSize = 2)

        assertEquals("20260621", result[0].date) // 수집된 항목에 날짜 기록
        assertNull(result[1].date)               // 미수집 항목은 날짜 없음
    }

    @Test
    fun `같은 번호를 여러 날에 열어도 수집 횟수는 1로 계산된다`() = runTest {
        // 동일한 no를 이틀에 걸쳐 열었을 때 중복 제거 후 1개로 처리
        val data = listOf(
            DailyCookieItemData(date = "20260620", list = listOf(CookieItemData(type = 0, no = 1, isOpened = true))),
            DailyCookieItemData(date = "20260621", list = listOf(CookieItemData(type = 0, no = 1, isOpened = true))),
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(type = 0, totalSize = 3)

        assertEquals(1, result.count { it.isCollected })
    }

    @Test
    fun `isOpened가 false인 쿠키는 no 값이 있어도 수집 완료로 보지 않는다`() = runTest {
        // 쿠키가 열리지 않은 상태(isOpened=false)라면 no 가 있어도 컬렉션에 포함하지 않음
        val data = listOf(
            DailyCookieItemData(
                date = "20260620",
                list = listOf(CookieItemData(type = 0, no = 1, isOpened = false))
            )
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(type = 0, totalSize = 2)

        assertTrue(result.none { it.isCollected })
    }
}

// ──────────────────────────────────────────────
// GetCollectionListUseCase
// 전체 타입의 컬렉션 목록을 합쳐서 반환하는 UseCase 테스트
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
        // Cheering 3개 + Comfort 2개 = 총 5개 항목
        coEvery { repository.getCookieDataList() } returns emptyList()

        val input = listOf(CookieType.Cheering to 3, CookieType.Comfort to 2)
        val result = useCase(input)

        assertEquals(5, result.size)
        assertEquals(3, result.count { it.type == CookieType.Cheering.type })
        assertEquals(2, result.count { it.type == CookieType.Comfort.type })
    }

    @Test
    fun `저장된 데이터가 없으면 모든 항목이 미수집 상태이다`() = runTest {
        // 레포지토리가 빈 리스트일 때 전체 미수집
        coEvery { repository.getCookieDataList() } returns emptyList()

        val result = useCase(listOf(CookieType.Cheering to 5))

        assertTrue(result.all { !it.isCollected })
    }

    @Test
    fun `타입별로 수집된 쿠키가 정확히 표시된다`() = runTest {
        // Cheering no=1 과 Comfort no=2 가 열려있을 때 해당 항목만 수집 완료
        val data = listOf(
            DailyCookieItemData(
                date = "20260620",
                list = listOf(
                    CookieItemData(type = CookieType.Cheering.type, no = 1, isOpened = true),
                    CookieItemData(type = CookieType.Comfort.type, no = 2, isOpened = true),
                )
            )
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(listOf(CookieType.Cheering to 3, CookieType.Comfort to 3))

        val cheeringNo1 = result.find { it.type == CookieType.Cheering.type && it.no == 1 }
        val comfortNo2 = result.find { it.type == CookieType.Comfort.type && it.no == 2 }
        assertTrue(cheeringNo1?.isCollected == true)
        assertTrue(comfortNo2?.isCollected == true)
    }

    @Test
    fun `같은 타입과 번호를 여러 날에 열어도 수집 횟수는 1로 계산된다`() = runTest {
        // (type=0, no=1) 조합이 이틀에 걸쳐 기록되어도 중복 제거 후 1개로 처리
        val data = listOf(
            DailyCookieItemData(date = "20260620", list = listOf(CookieItemData(type = 0, no = 1, isOpened = true))),
            DailyCookieItemData(date = "20260621", list = listOf(CookieItemData(type = 0, no = 1, isOpened = true))),
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(listOf(CookieType.Cheering to 5))

        assertEquals(1, result.count { it.isCollected })
    }
}

// ──────────────────────────────────────────────
// GetNewCookieNumberUseCase
// 아직 열리지 않은 쿠키 번호를 랜덤 반환하는 UseCase 테스트
// ──────────────────────────────────────────────

class GetNewCookieNumberUseCaseTest {

    private lateinit var repository: CookieRepository
    private lateinit var useCase: GetNewCookieNumberUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetNewCookieNumberUseCase(repository)
    }

    @Test
    fun `열린 쿠키가 없으면 1부터 size 사이의 번호를 반환한다`() = runTest {
        // 아직 어떤 쿠키도 열리지 않았을 때 전체 범위에서 랜덤 선택
        coEvery { repository.getCookieDataList() } returns emptyList()

        val result = useCase(type = 0, size = 5)

        assertTrue(result in 1..5)
    }

    @Test
    fun `모든 번호가 이미 열렸으면 0을 반환한다`() = runTest {
        // 1~3이 전부 열린 상태에서 size=3이면 후보가 없으므로 0 반환
        val data = listOf(
            DailyCookieItemData(
                date = "20260620",
                list = (1..3).map { no -> CookieItemData(type = 0, no = no) }
            )
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(type = 0, size = 3)

        assertEquals(0, result)
    }

    @Test
    fun `이미 열린 번호를 제외한 번호만 반환된다`() = runTest {
        // 1, 2, 4, 5가 열린 상태이면 남은 번호 3만 반환 가능
        val openedNos = listOf(1, 2, 4, 5)
        val data = listOf(
            DailyCookieItemData(
                date = "20260620",
                list = openedNos.map { no -> CookieItemData(type = 0, no = no) }
            )
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(type = 0, size = 5)

        assertEquals(3, result)
    }

    @Test
    fun `다른 타입의 열린 쿠키는 제외 목록에 포함되지 않는다`() = runTest {
        // type=1 의 쿠키가 열려있어도 type=0 의 번호 선택에는 영향을 주지 않음
        val data = listOf(
            DailyCookieItemData(
                date = "20260620",
                list = listOf(
                    CookieItemData(type = 1, no = 1), // 다른 타입
                    CookieItemData(type = 1, no = 2), // 다른 타입
                )
            )
        )
        coEvery { repository.getCookieDataList() } returns data

        val result = useCase(type = 0, size = 3)

        // type=0 은 아무것도 열리지 않았으므로 1~3 중 하나여야 함
        assertTrue(result in 1..3)
    }
}
