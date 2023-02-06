package com.hotmail.or_dvir.tracks.database.repositories

import com.hotmail.or_dvir.tracks.models.TrackedEventModel
import kotlinx.coroutines.flow.Flow

interface TrackedEventsRepository {
    fun getAllSortedByAlphabet(): Flow<List<TrackedEventModel>>
    suspend fun loadEventById(id: Int): TrackedEventModel
    suspend fun insert(event: TrackedEventModel): Long
    suspend fun delete(eventId: Int)
}
