package com.hotmail.or_dvir.tracks.database.repositories

import com.hotmail.or_dvir.tracks.models.TrackedEvent
import kotlinx.coroutines.flow.Flow

interface TrackedEventsRepository {
    fun getAllSortedByAlphabet(): Flow<List<TrackedEvent>>
    suspend fun insertOrReplace(event: TrackedEvent): Long
    suspend fun delete(eventId: Int)
}
