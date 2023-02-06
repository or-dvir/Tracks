package com.hotmail.or_dvir.tracks.database.repositories

import com.hotmail.or_dvir.tracks.database.daos.TrackedEventsDao
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.toEntity
import com.hotmail.or_dvir.tracks.toEvent
import com.hotmail.or_dvir.tracks.toEvents
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TrackedEventsRepositoryImpl @Inject constructor(
    private val dao: TrackedEventsDao,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : TrackedEventsRepository {

    // todo for now assume all operations are successful

    override fun getAllSortedByAlphabet(): Flow<List<TrackedEvent>> =
        dao.getAllSortedByAlphabet().map { it.toEvents() }

    override suspend fun loadEventById(id: Int): TrackedEvent {
        return withContext(dispatcher) {
            dao.loadEventById(id).toEvent()
        }
    }

    override suspend fun insert(event: TrackedEvent): Long {
        return shouldNotBeCancelled {
            dao.insert(event.toEntity())
        }
    }

    override suspend fun delete(eventId: Int) {
        return shouldNotBeCancelled {
            dao.delete(eventId)
        }
    }

    private suspend inline fun <T : Any> shouldNotBeCancelled(
        crossinline operation: suspend (coroutineScope: CoroutineScope) -> T
    ): T {
        return withContext(dispatcher) {
            scopeThatShouldNotBeCancelled.async {
                operation(this)
            }.await()
        }
    }
}
