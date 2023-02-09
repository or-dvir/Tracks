package com.hotmail.or_dvir.tracks

import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventInstanceEntity
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.models.TrackedEventOccurrence

@JvmName("TrackedEventEntities")
fun List<TrackedEvent>.toEntities() = this.map { it.toEntity() }
fun TrackedEvent.toEntity() = TrackedEventEntity(
    id = id,
    name = name
)

fun List<TrackedEventEntity>.toEvents() = this.map { it.toEvent() }
fun TrackedEventEntity.toEvent() = TrackedEvent(
    id = id,
    name = name
)

@JvmName("TrackedEventInstanceEntities")
fun List<TrackedEventOccurrence>.toEntities() = this.map { it.toEntity() }
fun TrackedEventOccurrence.toEntity() = TrackedEventInstanceEntity(
    id = id,
    startMillis = startMillis,
    endMillis = endMillis,
    note = note,
    eventId = eventId
)

fun List<TrackedEventInstanceEntity>.toEventInstances() = this.map { it.toEventInstance() }
fun TrackedEventInstanceEntity.toEventInstance() = TrackedEventOccurrence(
    startMillis = startMillis,
    endMillis = endMillis,
    note = note,
    id = id,
    eventId = eventId
)
