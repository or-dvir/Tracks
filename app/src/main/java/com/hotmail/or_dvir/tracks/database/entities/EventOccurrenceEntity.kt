package com.hotmail.or_dvir.tracks.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = EventOccurrenceEntity.TABLE_NAME)
data class EventOccurrenceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @Embedded(prefix = "start_")
    val startDate: EventOccurrenceDate,
    @Embedded(prefix = "start_")
    val startTime: EventOccurrenceTime?,
    @Embedded(prefix = "end_")
    val endDate: EventOccurrenceDate?,
    @Embedded(prefix = "end_")
    val endTime: EventOccurrenceTime?,
    @ColumnInfo(name = "note")
    val note: String,
    @ColumnInfo(name = COLUMN_EVENT_ID)
    // todo do i need to annotate this with @ForeignKey???
    val eventId: Int
) {
    companion object {
        const val TABLE_NAME = "TrackedEventsInstances"
        const val COLUMN_ID = "id"
        const val COLUMN_EVENT_ID = "eventId"
    }
}

data class EventOccurrenceDate(
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "month")
    val month: String,
    @ColumnInfo(name = "dayOfMonth")
    val dayOfMonth: Int
)

data class EventOccurrenceTime(
    @ColumnInfo(name = "hourOfDay")
    val hourOfDay: Int,
    @ColumnInfo(name = "minute")
    val minute: Int,
)