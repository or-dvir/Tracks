package com.hotmail.or_dvir.tracks.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = EventOccurrenceEntity.TABLE_NAME)
data class EventOccurrenceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @ColumnInfo(name = COLUMN_START_MILLIS)
    val startMillis: Long,
    @ColumnInfo(name = "endMillis")
    val endMillis: Long?,
    @ColumnInfo(name = "note")
    val note: String?,
    @ColumnInfo(name = COLUMN_EVENT_ID)
    // todo do i need to annotate this with @ForeignKey???
    val eventId: Int
) {
    companion object {
        const val TABLE_NAME = "TrackedEventsInstances"
        const val COLUMN_ID = "id"
        const val COLUMN_EVENT_ID = "eventId"
        const val COLUMN_START_MILLIS = "startMillis"
    }
}
