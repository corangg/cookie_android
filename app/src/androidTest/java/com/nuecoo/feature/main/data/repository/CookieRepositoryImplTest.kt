package com.nuecoo.feature.main.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nuecoo.core.data.database.NueCooDatabase
import com.nuecoo.feature.main.data.datasource.cookie.CookieLocalDataSourceImpl
import com.nuecoo.feature.main.domain.model.CookieItemData
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// ──────────────────────────────────────────────
// CookieRepositoryImpl 통합 테스트
// 실제 인메모리 DB + DataSource + Repository 전 계층을 연결하여
// 도메인 ↔ 로컬 모델 변환(Mapper)까지 검증
// ──────────────────────────────────────────────

@RunWith(AndroidJUnit4::class)
class CookieRepositoryImplTest {

    private lateinit var db: NueCooDatabase
    private lateinit var repository: CookieRepositoryImpl

    // 테스트용 도메인 모델: 열림 + 미열림 쿠키 혼합
    private val sampleData = DailyCookieItemData(
        date = "20260626",
        list = listOf(
            CookieItemData(type = 0, no = 1, isOpened = true),
            CookieItemData(type = 1, no = null, isOpened = false),
        )
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NueCooDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CookieRepositoryImpl(
            cookieDataSource = CookieLocalDataSourceImpl(db.cookieDao()),
            ioDispatcher = Dispatchers.IO,
            context = context,
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    // ── upsertDailyCookieData + getCookieDataList ──

    @Test
    fun 저장한_데이터를_getCookieDataList로_조회할_수_있다() = runBlocking {
        // upsert 후 목록 조회 시 동일한 날짜·항목 수를 가져야 함
        repository.upsertDailyCookieData(sampleData)

        val result = repository.getCookieDataList()

        assertEquals(1, result.size)
        assertEquals(sampleData.date, result[0].date)
        assertEquals(sampleData.list.size, result[0].list.size)
    }

    @Test
    fun Mapper가_도메인_모델의_모든_필드를_보존한다() = runBlocking {
        // DailyCookieItemData → Local → 역변환 시 각 필드 값이 유실되지 않아야 함
        val detailed = DailyCookieItemData(
            date = "20260626",
            list = listOf(
                CookieItemData(time = "14:30:00", type = 2, isFull = true, no = 3, isOpened = true),
            )
        )
        repository.upsertDailyCookieData(detailed)

        val cookie = repository.getCookieDataList()[0].list[0]

        assertEquals("14:30:00", cookie.time)
        assertEquals(2, cookie.type)
        assertEquals(true, cookie.isFull)
        assertEquals(3, cookie.no)
        assertEquals(true, cookie.isOpened)
    }

    @Test
    fun 동일한_날짜로_upsert하면_기존_데이터가_갱신된다() = runBlocking {
        // PrimaryKey(date)가 같으면 덮어써야 함
        repository.upsertDailyCookieData(sampleData)
        val updated = sampleData.copy(list = listOf(CookieItemData(type = 0, no = 5, isOpened = true)))
        repository.upsertDailyCookieData(updated)

        val result = repository.getCookieDataList()

        assertEquals(1, result.size)          // 중복 레코드 없음
        assertEquals(5, result[0].list[0].no) // 번호가 갱신됨
    }

    @Test
    fun 여러_날짜의_데이터를_모두_조회할_수_있다() = runBlocking {
        // 날짜가 다른 데이터를 각각 저장하면 전부 반환되어야 함
        val data1 = DailyCookieItemData(date = "20260620", list = emptyList())
        val data2 = DailyCookieItemData(date = "20260626", list = emptyList())
        repository.upsertDailyCookieData(data1)
        repository.upsertDailyCookieData(data2)

        val result = repository.getCookieDataList()

        assertEquals(2, result.size)
    }

    // ── getFlowDailyCookieData ──

    @Test
    fun 데이터가_없으면_getFlowDailyCookieData는_null을_방출한다() = runBlocking {
        val result = repository.getFlowDailyCookieData().first()

        assertNull(result)
    }

    @Test
    fun 저장_후_getFlowDailyCookieData는_최신_데이터를_방출한다() = runBlocking {
        // 가장 최근 날짜(DESC LIMIT 1) 데이터를 Flow로 관찰
        repository.upsertDailyCookieData(DailyCookieItemData("20260620", emptyList()))
        repository.upsertDailyCookieData(sampleData) // 20260626

        val result = repository.getFlowDailyCookieData().first()

        assertEquals("20260626", result?.date)
        assertEquals(2, result?.list?.size)
    }

    @Test
    fun getFlowDailyCookieData가_도메인_모델로_매핑된다() = runBlocking {
        // Flow로 받은 값도 Mapper를 거쳐 DailyCookieItemData 타입이어야 함
        repository.upsertDailyCookieData(sampleData)

        val result = repository.getFlowDailyCookieData().first()

        assertEquals(0, result?.list?.get(0)?.type)
        assertEquals(1, result?.list?.get(0)?.no)
        assertNull(result?.list?.get(1)?.no)
    }

    // ── getFlowCookieDataList ──

    @Test
    fun 데이터가_없으면_getFlowCookieDataList는_빈_리스트를_방출한다() = runBlocking {
        val result = repository.getFlowCookieDataList().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun 저장_후_getFlowCookieDataList는_전체_목록을_방출한다() = runBlocking {
        repository.upsertDailyCookieData(DailyCookieItemData("20260624", emptyList()))
        repository.upsertDailyCookieData(sampleData)

        val result = repository.getFlowCookieDataList().first()

        assertEquals(2, result.size)
    }
}
