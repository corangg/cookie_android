package com.nuecoo.core.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nuecoo.core.data.database.NueCooDatabase
import com.nuecoo.core.data.model.local.LocalUserInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// ──────────────────────────────────────────────
// UserInfoDao 통합 테스트
// 인메모리 Room DB로 사용자 정보 단건 CRUD를 검증
// ──────────────────────────────────────────────

@RunWith(AndroidJUnit4::class)
class UserInfoDaoTest {

    private lateinit var db: NueCooDatabase
    private lateinit var dao: UserInfoDao

    // 테스트용 사용자 정보 (id=1 고정 → 싱글톤 레코드)
    private val sampleUser = LocalUserInfo(
        id = 1,
        email = "test@email.com",
        nickname = "테스터",
        phone = "01012345678",
        birth = "19990101",
        gender = true,
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NueCooDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.userInfoDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // ── 삽입·조회 ──

    @Test
    fun 사용자_삽입_후_getUserInfo로_조회된다() = runBlocking {
        // upsert 후 id=1 조건으로 조회하면 동일 객체가 반환되어야 함
        dao.upsertUserInfo(sampleUser)

        val result = dao.getUserInfo()

        assertEquals(sampleUser, result)
    }

    @Test
    fun 데이터가_없으면_getUserInfo는_null을_반환한다() = runBlocking {
        // 아직 아무것도 저장하지 않은 초기 상태
        assertNull(dao.getUserInfo())
    }

    @Test
    fun 모든_사용자_필드가_정확하게_저장된다() = runBlocking {
        // 이메일·닉네임·전화·생년월일·성별 등 각 필드 무결성 확인
        dao.upsertUserInfo(sampleUser)

        val result = dao.getUserInfo()!!

        assertEquals("test@email.com", result.email)
        assertEquals("테스터", result.nickname)
        assertEquals("01012345678", result.phone)
        assertEquals("19990101", result.birth)
        assertEquals(true, result.gender)
    }

    // ── Upsert (갱신) ──

    @Test
    fun 동일한_id로_upsert하면_기존_사용자_정보가_갱신된다() = runBlocking {
        // id=1 레코드가 이미 있으면 INSERT OR REPLACE로 덮어씀
        dao.upsertUserInfo(sampleUser)
        val updated = sampleUser.copy(nickname = "새닉네임", phone = "01099998888")
        dao.upsertUserInfo(updated)

        val result = dao.getUserInfo()

        assertEquals("새닉네임", result?.nickname)
        assertEquals("01099998888", result?.phone)
    }

    // ── 삭제 ──

    @Test
    fun deleteUser_후_getUserInfo는_null을_반환한다() = runBlocking {
        // 사용자 삭제 후 조회하면 null이어야 함
        dao.upsertUserInfo(sampleUser)

        dao.deleteUser()

        assertNull(dao.getUserInfo())
    }

    // ── getUserInfoFlow (Flow) ──

    @Test
    fun 데이터가_없으면_getUserInfoFlow는_null을_방출한다() = runBlocking {
        val result = dao.getUserInfoFlow().first()

        assertNull(result)
    }

    @Test
    fun 삽입_후_getUserInfoFlow는_사용자_정보를_방출한다() = runBlocking {
        dao.upsertUserInfo(sampleUser)

        val result = dao.getUserInfoFlow().first()

        assertEquals(sampleUser.email, result?.email)
        assertEquals(sampleUser.nickname, result?.nickname)
    }
}
