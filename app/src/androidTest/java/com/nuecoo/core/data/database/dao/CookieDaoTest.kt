package com.nuecoo.core.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nuecoo.core.data.database.NueCooDatabase
import com.nuecoo.core.data.model.local.LocalCookieData
import com.nuecoo.core.data.model.local.LocalDailyCookieData
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
// CookieDao 통합 테스트
// 인메모리 Room DB를 사용해 실제 SQL 쿼리와 TypeConverter 동작을 검증
// ──────────────────────────────────────────────

@RunWith(AndroidJUnit4::class)
class CookieDaoTest {

    private lateinit var db: NueCooDatabase
    private lateinit var dao: CookieDao

    // 테스트용 쿠키 데이터: 열림 1개 + 미열림 1개
    private val sampleData = LocalDailyCookieData(
        date = "20260626",
        list = listOf(
            LocalCookieData(type = 0, no = 1, isOpened = true),
            LocalCookieData(type = 1, no = null, isOpened = false),
        )
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NueCooDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.cookieDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // ── 단건 삽입·조회 ──

    @Test
    fun 데이터를_삽입하면_getCookieList에서_조회된다() = runBlocking {
        // upsert 후 전체 목록 조회 시 해당 레코드가 존재해야 함
        dao.upsertCookieData(sampleData)

        val result = dao.getCookieList()

        assertEquals(1, result.size)
        assertEquals(sampleData.date, result[0].date)
        assertEquals(2, result[0].list.size)
    }

    @Test
    fun TypeConverter가_쿠키_리스트를_올바르게_직렬화_역직렬화한다() = runBlocking {
        // Gson 기반 TypeConverter가 list 필드를 정확히 보존하는지 확인
        dao.upsertCookieData(sampleData)

        val result = dao.getCookieList()[0].list

        assertEquals(0, result[0].type)
        assertEquals(1, result[0].no)
        assertEquals(true, result[0].isOpened)
        assertEquals(1, result[1].type)
        assertNull(result[1].no)
        assertEquals(false, result[1].isOpened)
    }

    // ── Upsert (삽입 / 갱신) ──

    @Test
    fun 동일한_date로_upsert하면_기존_레코드가_갱신된다() = runBlocking {
        // PrimaryKey(date)가 같으면 INSERT OR REPLACE 동작
        dao.upsertCookieData(sampleData)
        val updated = sampleData.copy(list = listOf(LocalCookieData(type = 0, no = 5, isOpened = true)))
        dao.upsertCookieData(updated)

        val result = dao.getCookieList()

        assertEquals(1, result.size)         // 중복 삽입 없음
        assertEquals(5, result[0].list[0].no) // 번호가 갱신됨
    }

    @Test
    fun 다른_date로_upsert하면_레코드가_추가된다() = runBlocking {
        // 날짜가 다르면 별도 레코드로 저장
        dao.upsertCookieData(sampleData)
        dao.upsertCookieData(sampleData.copy(date = "20260627"))

        assertEquals(2, dao.getCookieList().size)
    }

    // ── getLastDailyCookieData ──

    @Test
    fun getLastDailyCookieData는_가장_최근_날짜의_데이터를_반환한다() = runBlocking {
        // DATE DESC LIMIT 1 이므로 가장 큰 날짜 문자열이 반환되어야 함
        dao.upsertCookieListData(
            listOf(
                LocalDailyCookieData(date = "20260620", list = emptyList()),
                LocalDailyCookieData(date = "20260626", list = emptyList()),
                LocalDailyCookieData(date = "20260624", list = emptyList()),
            )
        )

        val result = dao.getLastDailyCookieData()

        assertEquals("20260626", result?.date)
    }

    @Test
    fun 데이터가_없으면_getLastDailyCookieData는_null을_반환한다() = runBlocking {
        assertNull(dao.getLastDailyCookieData())
    }

    // ── observeLastDailyCookieData (Flow) ──

    @Test
    fun 데이터가_없으면_observeLastDailyCookieData는_null을_방출한다() = runBlocking {
        val result = dao.observeLastDailyCookieData().first()

        assertNull(result)
    }

    @Test
    fun 삽입_후_observeLastDailyCookieData는_최신_데이터를_방출한다() = runBlocking {
        dao.upsertCookieData(sampleData)

        val result = dao.observeLastDailyCookieData().first()

        assertEquals(sampleData.date, result?.date)
        assertEquals(2, result?.list?.size)
    }

    // ── observeCookieList (Flow) ──

    @Test
    fun observeCookieList는_삽입된_전체_목록을_방출한다() = runBlocking {
        dao.upsertCookieData(sampleData)
        dao.upsertCookieData(sampleData.copy(date = "20260625"))

        val result = dao.observeCookieList().first()

        assertEquals(2, result.size)
    }

    @Test
    fun 데이터가_없으면_observeCookieList는_빈_리스트를_방출한다() = runBlocking {
        val result = dao.observeCookieList().first()

        assertTrue(result.isEmpty())
    }

    // ── deleteCookieData ──

    @Test
    fun deleteCookieData는_모든_레코드를_삭제한다() = runBlocking {
        dao.upsertCookieData(sampleData)
        dao.upsertCookieData(sampleData.copy(date = "20260625"))

        dao.deleteCookieData()

        assertTrue(dao.getCookieList().isEmpty())
    }
}
