package com.hotmail.or_dvir.tracks

import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceEntity
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.models.TrackedEvent

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

@JvmName("TrackedEventOccurrenceEntities")
fun List<EventOccurrence>.toEntities() = this.map { it.toEntity() }
fun EventOccurrence.toEntity() = EventOccurrenceEntity(
    id = id,
    startMillis = startMillis,
    endMillis = endMillis,
    note = note,
    eventId = eventId
)

fun List<EventOccurrenceEntity>.toEventOccurrence() = this.map { it.toEventOccurrence() }
fun EventOccurrenceEntity.toEventOccurrence() = EventOccurrence(
    startMillis = startMillis,
    endMillis = endMillis,
    note = note,
    id = id,
    eventId = eventId
)
