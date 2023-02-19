package com.hotmail.or_dvir.tracks.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceEntity
import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceEntity.Companion.COLUMN_EVENT_ID
import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceEntity.Companion.TABLE_NAME
import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceEntity.Companion.COLUMN_ID
import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceEntity.Companion.COLUMN_START_MILLIS
import kotlinx.coroutines.flow.Flow

@Dao
interface EventOccurrencesDao {
    @Query("SELECT * FROM $TABLE_NAME " +
            "WHERE $COLUMN_EVENT_ID = :eventId " +
            "ORDER BY $COLUMN_START_MILLIS DESC")
    fun getAllOccurrencesByStartDateDesc(eventId: Int): Flow<List<EventOccurrenceEntity>>

    @Insert
    suspend fun insert(occurrence: EventOccurrenceEntity): Long

    @Query("DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = :occurrenceId")
    suspend fun delete(occurrenceId: Int)
}
