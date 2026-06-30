package com.nuecoo.core.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nuecoo.core.data.database.NueCooDatabase
import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// ──────────────────────────────────────────────
// CookieEventDao 통합 테스트
// 인메모리 Room DB를 사용해 실제 SQL 쿼리와 TypeConverter 동작을 검증
// ──────────────────────────────────────────────

@RunWith(AndroidJUnit4::class)
class CookieDaoTest {

    private lateinit var db: NueCooDatabase
    private lateinit var dao: CookieEventDao

    private val sampleEvent = CookieEventEntity(
        eventId = "event-001",
        datetime = "202606261000",
        claimDate = "20260626",
        type = 0,
        cookieNo = null,
        syncStatus = CookieSyncStatus.PENDING
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NueCooDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.cookieEventDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun 이벤트를_삽입하면_getAllEvents에서_조회된다() = runBlocking {
        dao.insert(sampleEvent)
        val result = dao.getAllEvents()
        assertEquals(1, result.size)
        assertEquals("event-001", result[0].eventId)
    }

    @Test
    fun SyncStatusConverter가_올바르게_직렬화_역직렬화한다() = runBlocking {
        dao.insert(sampleEvent)
        val result = dao.getById("event-001")
        assertNotNull(result)
        assertEquals(CookieSyncStatus.PENDING, result!!.syncStatus)
    }

    @Test
    fun updateStatus는_syncStatus와_cookieNo를_갱신한다() = runBlocking {
        dao.insert(sampleEvent)
        dao.updateStatus("event-001", CookieSyncStatus.SAVED, cookieNo = 3)
        val result = dao.getById("event-001")
        assertEquals(CookieSyncStatus.SAVED, result?.syncStatus)
        assertEquals(3, result?.cookieNo)
    }

    @Test
    fun updateStatus에서_cookieNo가_null이면_기존_값을_유지한다() = runBlocking {
        val withNo = sampleEvent.copy(cookieNo = 5, syncStatus = CookieSyncStatus.SAVED)
        dao.insert(withNo)
        dao.updateStatus("event-001", CookieSyncStatus.SYNC_FAILED, cookieNo = null)
        val result = dao.getById("event-001")
        assertEquals(CookieSyncStatus.SYNC_FAILED, result?.syncStatus)
        assertEquals(5, result?.cookieNo)
    }

    @Test
    fun observeEventsForDate는_해당_날짜_이벤트만_방출한다() = runBlocking {
        dao.insert(sampleEvent)
        dao.insert(sampleEvent.copy(eventId = "event-002", claimDate = "20260625"))
        val result = dao.observeEventsForDate("20260626").first()
        assertEquals(1, result.size)
        assertEquals("event-001", result[0].eventId)
    }

    @Test
    fun getAllByStatus는_해당_상태_이벤트만_반환한다() = runBlocking {
        dao.insert(sampleEvent) // PENDING
        dao.insert(sampleEvent.copy(eventId = "event-002", syncStatus = CookieSyncStatus.SAVED))
        val pending = dao.getAllByStatus(CookieSyncStatus.PENDING)
        assertEquals(1, pending.size)
        assertEquals("event-001", pending[0].eventId)
    }

    @Test
    fun 데이터가_없으면_observeEventsForDate는_빈_리스트를_방출한다() = runBlocking {
        val result = dao.observeEventsForDate("20260626").first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun 존재하지_않는_eventId로_getById하면_null을_반환한다() = runBlocking {
        assertNull(dao.getById("non-existent"))
    }
}
