package com.hotmail.or_dvir.tracks.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hotmail.or_dvir.tracks.database.daos.EventOccurrencesDao
import com.hotmail.or_dvir.tracks.database.daos.TrackedEventsDao
import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceEntity
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity

@Database(
    entities = [
        TrackedEventEntity::class,
        EventOccurrenceEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackedEventsDao(): TrackedEventsDao
    abstract fun eventOccurrencesDao(): EventOccurrencesDao
}
