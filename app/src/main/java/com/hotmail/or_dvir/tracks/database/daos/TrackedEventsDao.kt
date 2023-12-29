package com.hotmail.or_dvir.tracks.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity.Companion.COLUMN_ID
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity.Companion.COLUMN_NAME
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity.Companion.TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedEventsDao {
    @Query("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_NAME")
    fun getAllSortedByAlphabet(): Flow<List<TrackedEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(event: TrackedEventEntity): Long

    @Update
    suspend fun update(event: TrackedEventEntity): Int

    // todo also delete all event instances on other table!!!
    @Query("DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = :eventId")
    suspend fun delete(eventId: Int)
}
