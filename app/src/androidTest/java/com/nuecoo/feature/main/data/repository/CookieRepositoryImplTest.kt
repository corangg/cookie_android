package com.nuecoo.feature.main.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nuecoo.core.data.database.NueCooDatabase
import com.nuecoo.core.domain.model.CookieSyncStatus
import com.nuecoo.feature.main.data.datasource.local.CookieEventDataSourceImpl
import com.nuecoo.feature.main.data.datasource.local.CookieTypeCountLocalDataSourceImpl
import com.nuecoo.feature.main.data.datasource.remote.CookieRemoteDataSource
import com.nuecoo.feature.main.data.worker.CookieSyncScheduler
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// ──────────────────────────────────────────────
// CookieRepositoryImpl 통합 테스트
// 실제 인메모리 DB + 로컬 데이터소스 계층을 연결하여 도메인 모델 변환 검증
// 원격 동기화(CookieRemoteDataSource)와 WorkManager 스케줄링(CookieSyncScheduler)은
// 이 테스트가 다루는 시나리오(로컬 저장/조회)와 무관하므로 목킹한다.
//
// [아키텍처 변경 메모]
// CookieRepositoryImpl은 더 이상 CookieDao/WorkManager를 직접 받지 않는다.
// CookieEventDataSource·CookieTypeCountLocalDataSource·CookieRemoteDataSource·
// CookieSyncScheduler로 계층이 분리되었다.
// ──────────────────────────────────────────────

@RunWith(AndroidJUnit4::class)
class CookieRepositoryImplTest {

    private lateinit var db: NueCooDatabase
    private lateinit var repository: CookieRepositoryImpl
    private lateinit var cookieRemoteDataSource: CookieRemoteDataSource
    private lateinit var cookieSyncScheduler: CookieSyncScheduler

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NueCooDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cookieRemoteDataSource = mockk(relaxed = true)
        cookieSyncScheduler = mockk(relaxed = true)
        repository = CookieRepositoryImpl(
            cookieEventDataSource = CookieEventDataSourceImpl(db.cookieEventDao()),
            cookieTypeCountLocalDataSource = CookieTypeCountLocalDataSourceImpl(db.cookieTypeCountDao()),
            cookieRemoteDataSource = cookieRemoteDataSource,
            ioDispatcher = Dispatchers.IO,
            cookieSyncScheduler = cookieSyncScheduler,
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
