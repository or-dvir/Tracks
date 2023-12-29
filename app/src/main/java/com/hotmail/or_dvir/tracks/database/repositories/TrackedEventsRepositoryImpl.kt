package com.hotmail.or_dvir.tracks.database.repositories

import com.hotmail.or_dvir.tracks.database.daos.TrackedEventsDao
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.toEntity
import com.hotmail.or_dvir.tracks.toEvents
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackedEventsRepositoryImpl @Inject constructor(
    private val dao: TrackedEventsDao,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : TrackedEventsRepository {

    // todo for now assume all operations are successful

    override fun getAllSortedByAlphabet(): Flow<List<TrackedEvent>> =
        dao.getAllSortedByAlphabet().map { it.toEvents() }

    override suspend fun insertOrReplace(event: TrackedEvent): Long {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.insertOrReplace(event.toEntity())
        }
    }

    override suspend fun update(event: TrackedEvent): Int {
        return shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.update(event.toEntity())
        }
    }

    override suspend fun delete(eventId: Int) {
        shouldNotBeCancelled(
            dispatcher = dispatcher,
            scopeThatShouldNotBeCancelled = scopeThatShouldNotBeCancelled
        ) {
            dao.delete(eventId)
        }
    }
}
