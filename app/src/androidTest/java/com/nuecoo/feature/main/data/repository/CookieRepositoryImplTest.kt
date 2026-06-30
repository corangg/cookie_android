package com.nuecoo.feature.main.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkManager
import com.nuecoo.core.data.database.NueCooDatabase
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

// ──────────────────────────────────────────────
// CookieRepositoryImpl 통합 테스트
// 실제 인메모리 DB + Repository 계층을 연결하여 도메인 모델 변환 검증
// WorkManager는 목킹하여 Room 동작에만 집중
// ──────────────────────────────────────────────

@RunWith(AndroidJUnit4::class)
class CookieRepositoryImplTest {

    private lateinit var db: NueCooDatabase
    private lateinit var repository: CookieRepositoryImpl
    private lateinit var workManager: WorkManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NueCooDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        workManager = mockk(relaxed = true)
        repository = CookieRepositoryImpl(
            cookieDao = db.cookieEventDao(),
            workManager = workManager
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun openCookie는_PENDING_이벤트를_저장한다() = runBlocking {
        repository.openCookie(type = 0)

        val events = repository.getAllEvents()
        assertEquals(1, events.size)
        assertEquals(0, events[0].type)
        assertEquals(CookieSyncStatus.PENDING, events[0].syncStatus)
    }

    @Test
    fun openCookie는_claimDate에_오늘_날짜를_사용한다() = runBlocking {
        repository.openCookie(type = 1)

        val events = repository.getAllEvents()
        val today = java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
        )
        assertEquals(today, events[0].claimDate)
    }

    @Test
    fun observeEventsForToday는_오늘_이벤트를_방출한다() = runBlocking {
        repository.openCookie(type = 0)

        val events = repository.observeEventsForToday().first()
        assertEquals(1, events.size)
    }

    @Test
    fun observeAllEvents는_전체_이벤트를_방출한다() = runBlocking {
        repository.openCookie(type = 0)
        repository.openCookie(type = 1)

        val events = repository.observeAllEvents().first()
        assertEquals(2, events.size)
    }

    @Test
    fun getAllEvents가_도메인_모델로_변환된다() = runBlocking {
        repository.openCookie(type = 2)

        val result = repository.getAllEvents()
        assertEquals(2, result[0].type)
        assertTrue(result[0].eventId.isNotBlank())
    }
}
