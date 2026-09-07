package com.nuecoo.feature.main.presentation.collection

import com.nuecoo.feature.main.domain.model.CollectionDisplayItem
import com.nuecoo.feature.main.domain.model.CollectionSortType
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.usecase.GetCollectionListUseCase
import com.nuecoo.feature.main.presentation.collection.viewmodel.CollectionViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────
// CollectionViewModel
// 컬렉션 목록의 타입 필터·정렬·수집 필터를 관리하는 ViewModel 테스트
//
// [아키텍처 변경 메모]
// initCollectionList()는 더 이상 파라미터를 받는 public 함수가 아니다.
// GetCollectionListUseCase가 repository.getCookieCount()로 타입별 개수를
// 내부에서 직접 조회하므로, ViewModel의 init 블록에서 인자 없이 자동 호출된다.
// 테스트 디스패처가 Unconfined이므로 ViewModel 생성 시점에 이미 로드가 끝난다.
// ──────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getCollectionListUseCase: GetCollectionListUseCase
    private lateinit var viewModel: CollectionViewModel

    // 테스트용 데이터: Cheering 3개(2개 수집), Comfort 2개(1개 수집)
    private val allItems = listOf(
        CollectionDisplayItem(no = 1, type = CookieType.Cheering.type, isCollected = true, date = "20260620"),
        CollectionDisplayItem(no = 2, type = CookieType.Cheering.type, isCollected = false, date = null),
        CollectionDisplayItem(no = 3, type = CookieType.Cheering.type, isCollected = true, date = "20260622"),
        CollectionDisplayItem(no = 1, type = CookieType.Comfort.type, isCollected = false, date = null),
        CollectionDisplayItem(no = 2, type = CookieType.Comfort.type, isCollected = true, date = "20260621"),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getCollectionListUseCase = mockk()
        coEvery { getCollectionListUseCase() } returns allItems
        viewModel = CollectionViewModel(
            getCollectionListUseCase = getCollectionListUseCase,
            mainDispatcher = Dispatchers.Main as MainCoroutineDispatcher,
            defaultDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── 초기 상태 (ViewModel 생성 시 init 블록이 자동으로 목록을 로드한다) ──

    @Test
    fun `생성 직후 UseCase가 반환한 전체 항목이 로드된다`() {
        assertEquals(allItems.size, viewModel.items.value.size)
    }

    @Test
    fun `초기 selectedCookieType은 null이다`() {
        // 타입 필터가 설정되지 않은 초기 상태
        assertNull(viewModel.selectedCookieType.value)
    }

    @Test
    fun `초기 정렬 방식은 BY_NO이다`() {
        // 기본 정렬은 번호 순
        assertEquals(CollectionSortType.BY_NO, viewModel.sortType.value)
    }

    @Test
    fun `초기 showCollectedOnly는 false이다`() {
        // 기본값은 수집 여부 필터 없이 전체 표시
        assertFalse(viewModel.showCollectedOnly.value)
    }

    // ── setSelectedCookieType ──

    @Test
    fun `null 타입으로 설정하면 전체 항목이 표시된다`() {
        // 타입 필터를 null로 해제하면 전체 항목 복원
        viewModel.setSelectedCookieType(null)
        assertEquals(allItems.size, viewModel.items.value.size)
    }

    @Test
    fun `특정 타입으로 설정하면 해당 타입 항목만 표시된다`() {
        // Cheering 타입 선택 시 Cheering 3개만 남아야 함
        viewModel.setSelectedCookieType(CookieType.Cheering)

        val result = viewModel.items.value
        assertTrue(result.all { it.type == CookieType.Cheering.type })
        assertEquals(3, result.size)
    }

    @Test
    fun `타입 선택 시 selectedCookieType 상태가 업데이트된다`() {
        // 타입을 선택하면 selectedCookieType StateFlow 값이 변경되어야 함
        viewModel.setSelectedCookieType(CookieType.Comfort)
        assertEquals(CookieType.Comfort, viewModel.selectedCookieType.value)
    }

    // ── setSortType ──

    @Test
    fun `BY_NO 정렬 시 번호 오름차순으로 정렬된다`() {
        // no 값이 작은 순서대로 items 가 정렬되어야 함
        viewModel.setSelectedCookieType(CookieType.Cheering)
        viewModel.setSortType(CollectionSortType.BY_NO)

        val nos = viewModel.items.value.map { it.no }
        assertEquals(nos.sorted(), nos)
    }

    @Test
    fun `BY_DATE 정렬 시 수집된 항목이 미수집 항목보다 먼저 나온다`() {
        // 날짜가 있는(수집) 항목이 앞에, 날짜가 없는(미수집) 항목이 뒤에 배치됨
        viewModel.setSelectedCookieType(null)
        viewModel.setSortType(CollectionSortType.BY_DATE)

        val items = viewModel.items.value
        val lastUncollectedIndex = items.indexOfLast { !it.isCollected }
        val firstCollectedIndex = items.indexOfFirst { it.isCollected }

        assertTrue(lastUncollectedIndex > firstCollectedIndex || items.none { !it.isCollected })
    }

    @Test
    fun `BY_DATE 정렬 시 수집된 항목끼리는 날짜 오름차순으로 정렬된다`() {
        // 수집 완료 항목들은 열린 날짜 순으로 정렬 (오래된 것이 먼저)
        viewModel.setSortType(CollectionSortType.BY_DATE)

        val collectedDates = viewModel.items.value
            .filter { it.isCollected }
            .mapNotNull { it.date }

        assertEquals(collectedDates.sorted(), collectedDates)
    }

    // ── setShowCollectedOnly ──

    @Test
    fun `showCollectedOnly=true로 설정하면 수집 완료 항목만 표시된다`() {
        // isCollected=false 인 항목은 보이지 않아야 함
        viewModel.setShowCollectedOnly(true)

        assertTrue(viewModel.items.value.all { it.isCollected })
    }

    @Test
    fun `showCollectedOnly=false로 해제하면 전체 항목이 다시 표시된다`() {
        // 필터를 켰다가 끄면 원래 전체 목록으로 복원
        viewModel.setShowCollectedOnly(true)
        viewModel.setShowCollectedOnly(false)

        assertEquals(allItems.size, viewModel.items.value.size)
    }

    // ── onOpenedItem / clearSelectedItem ──

    @Test
    fun `onOpenedItem 호출 시 selectedItem이 설정된다`() {
        // 항목을 클릭하면 selectedItem 에 해당 항목이 저장됨
        val item = allItems.first()
        viewModel.onOpenedItem(item)
        assertEquals(item, viewModel.selectedItem.value)
    }

    @Test
    fun `clearSelectedItem 호출 시 selectedItem이 null로 초기화된다`() {
        // 상세 화면을 닫으면 선택 항목이 해제되어야 함
        viewModel.onOpenedItem(allItems.first())
        viewModel.clearSelectedItem()
        assertNull(viewModel.selectedItem.value)
    }
}
