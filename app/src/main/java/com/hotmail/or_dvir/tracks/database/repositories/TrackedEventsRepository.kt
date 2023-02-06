package com.hotmail.or_dvir.tracks.database.repositories

import com.hotmail.or_dvir.tracks.models.TrackedEventModel
import kotlinx.coroutines.flow.Flow

interface TrackedEventsRepository {
    fun getAllSortedByStartDescending(): Flow<List<TrackedEventModel>>
    suspend fun loadWindowById(id: Int): TrackedEventModel
    suspend fun insertAll(vararg windows: TrackedEventModel): List<Long>
    suspend fun delete(windowId: Int)
}
