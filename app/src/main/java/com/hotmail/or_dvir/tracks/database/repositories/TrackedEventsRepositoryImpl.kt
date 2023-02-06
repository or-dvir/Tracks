package com.hotmail.or_dvir.tracks.database.repositories

import com.hotmail.or_dvir.tracks.database.daos.TrackedEventsDao
import com.hotmail.or_dvir.tracks.models.TrackedEventModel
import com.hotmail.or_dvir.tracks.toEntity
import com.hotmail.or_dvir.tracks.toModel
import com.hotmail.or_dvir.tracks.toModels
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

    override fun getAllSortedByStartDescending(): Flow<List<TrackedEventModel>> =
        dao.getAllSortedByStartDescending().map { it.toModels() }

    override suspend fun loadWindowById(id: Int): TrackedEventModel {
        return withContext(dispatcher) {
            dao.loadEventById(id).toModel()
        }
    }

    override suspend fun insertAll(vararg windows: TrackedEventModel): List<Long> {
        return shouldNotBeCancelled {
            dao.insertAll(windows.map { it.toEntity() })
        }
    }

    override suspend fun delete(windowId: Int) {
        return shouldNotBeCancelled {
            dao.delete(windowId)
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
