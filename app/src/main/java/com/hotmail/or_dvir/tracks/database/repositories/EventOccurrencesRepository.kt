package com.hotmail.or_dvir.tracks.database.repositories

import com.hotmail.or_dvir.tracks.models.EventOccurrence
import kotlinx.coroutines.flow.Flow

interface EventOccurrencesRepository {
    fun getAllByStartDateDesc(eventId: Int): Flow<List<EventOccurrence>>
    suspend fun insertOrReplace(occurrence: EventOccurrence): Long
    suspend fun delete(occurrenceId: Int)
}
