package com.hotmail.or_dvir.tracks.database.repositories

import com.hotmail.or_dvir.tracks.database.daos.EventOccurrencesDao
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.toEntity
import com.hotmail.or_dvir.tracks.toEventOccurrence
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventOccurrencesRepositoryImpl @Inject constructor(
    private val dao: EventOccurrencesDao,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : EventOccurrencesRepository {

    // todo for now assume all operations are successful
    
    override fun getAllByStartDateDesc(eventId: Int): Flow<List<EventOccurrence>> =
        dao.getAllByStartDateDesc(eventId).map { it.toEventOccurrence() }

    override suspend fun insert(occurrence: EventOccurrence): Long {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.insert(occurrence.toEntity())
        }
    }

    override suspend fun delete(occurrenceId: Int) {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.delete(occurrenceId)
        }
    }
}
