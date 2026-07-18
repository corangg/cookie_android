package com.nuecoo.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nuecoo.core.data.model.local.CookieEventEntity
import com.nuecoo.core.data.model.local.CookieTypeCountEntity
import com.nuecoo.core.data.model.local.LocalTypeCollectedCount
import com.nuecoo.feature.main.domain.model.CookieSyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CookieEventDao {
    @Insert
    suspend fun insert(event: CookieEventEntity)

    @Query("SELECT * FROM CookieEventEntity WHERE claimDate = :date")
    fun observeEventsForDate(date: String): Flow<List<CookieEventEntity>>

    @Query("SELECT * FROM CookieEventEntity")
    fun observeAllEvents(): Flow<List<CookieEventEntity>>

    @Query("SELECT * FROM CookieEventEntity")
    suspend fun getAllEvents(): List<CookieEventEntity>

    @Query("SELECT * FROM CookieEventEntity WHERE eventId = :eventId")
    suspend fun getById(eventId: String): CookieEventEntity?

    @Query("""
        UPDATE CookieEventEntity
        SET syncStatus = :status,
            cookieNo = COALESCE(:cookieNo, cookieNo),
            message = COALESCE(:message, message),
            viaTicketGroupId = COALESCE(:ticketGroupId, viaTicketGroupId)
        WHERE eventId = :eventId
    """)
    suspend fun updateStatus(
        eventId: String,
        status: CookieSyncStatus,
        cookieNo: Int? = null,
        message: String? = null,
        ticketGroupId: String? = null
    )

    @Query("""
    SELECT COUNT(DISTINCT cookieNo) FROM CookieEventEntity 
    WHERE type = :type AND syncStatus IN ('SAVED', 'SAVED_VIA_TICKET')
""")
    suspend fun getDistinctCollectedCount(type: Int): Int

    @Query("""
    SELECT COUNT(DISTINCT cookieNo) FROM CookieEventEntity 
    WHERE type = :type AND syncStatus IN ('SAVED', 'SAVED_VIA_TICKET')
""")
    fun observeDistinctCollectedCount(type: Int): Flow<Int>

    @Query("""
    SELECT type, COUNT(DISTINCT cookieNo) AS count FROM CookieEventEntity 
    WHERE syncStatus IN ('SAVED', 'SAVED_VIA_TICKET')
    GROUP BY type
""")
    fun observeDistinctCollectedCounts(): Flow<List<LocalTypeCollectedCount>>

    @Query("SELECT * FROM CookieEventEntity WHERE syncStatus = :status")
    suspend fun getAllByStatus(status: CookieSyncStatus): List<CookieEventEntity>

    @Query("DELETE FROM CookieEventEntity")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(events: List<CookieEventEntity>)

    @Query("""
        SELECT DISTINCT claimDate FROM CookieEventEntity 
        ORDER BY claimDate DESC
    """)
    fun observeAllClaimDates(): Flow<List<String>>
}

@Dao
interface CookieTypeCountDao {
    @Upsert
    suspend fun upsertCookieTypeCounts(entities: List<CookieTypeCountEntity>)

    @Query("SELECT * FROM CookieTypeCountEntity")
    suspend fun getCookieTypeCount(): List<CookieTypeCountEntity>

    @Query("SELECT maxCount FROM CookieTypeCountEntity WHERE type = :type")
    suspend fun getMaxCount(type: Int): Int?

    @Query("SELECT * FROM CookieTypeCountEntity")
    fun getCookieTypeCountFlow(): Flow<List<CookieTypeCountEntity>>
}
