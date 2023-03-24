package com.hotmail.or_dvir.tracks

import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceDate
import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceEntity
import com.hotmail.or_dvir.tracks.database.entities.EventOccurrenceTime
import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month

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
    note = note,
    eventId = eventId,
    startDate = EventOccurrenceDate(
        year = startDate.year,
        month = startDate.month.name,
        dayOfMonth = startDate.dayOfMonth
    ),
    startTime = startTime?.let {
        EventOccurrenceTime(
            hourOfDay = it.hour,
            minute = it.minute
        )
    },
    endDate = endDate?.let {
        EventOccurrenceDate(
            year = it.year,
            month = it.month.name,
            dayOfMonth = it.dayOfMonth
        )
    },
    endTime = endTime?.let {
        EventOccurrenceTime(
            hourOfDay = it.hour,
            minute = it.minute
        )
    }
)

fun List<EventOccurrenceEntity>.toEventOccurrences() = this.map { it.toEventOccurrence() }
fun EventOccurrenceEntity.toEventOccurrence() = EventOccurrence(
    id = id,
    note = note,
    eventId = eventId,
    startDate = LocalDate.of(
        startDate.year,
        Month.valueOf(startDate.month),
        startDate.dayOfMonth,
    ),
    startTime = startTime?.let {
        LocalTime.of(it.hourOfDay, it.minute)
    },
    endDate = endDate?.let {
        LocalDate.of(
            it.year,
            Month.valueOf(it.month),
            it.dayOfMonth,
        )
    },
    endTime = endTime?.let {
        LocalTime.of(it.hourOfDay, it.minute)
    }
)
