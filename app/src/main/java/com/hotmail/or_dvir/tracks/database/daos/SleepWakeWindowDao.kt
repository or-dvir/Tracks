package com.hotmail.or_dvir.tracks.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity.Companion.COLUMN_ID
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity.Companion.COLUMN_START_MILLIS
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity.Companion.TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedEventsDao {
    @Query("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_START_MILLIS DESC")
    fun getAllSortedByStartDescending(): Flow<List<TrackedEventEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = :id")
    suspend fun loadEventById(id: Int): TrackedEventEntity

    @Insert
    suspend fun insertAll(windows: List<TrackedEventEntity>): List<Long>

    @Query("DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = :windowId")
    suspend fun delete(windowId: Int)
}
