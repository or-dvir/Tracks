package com.hotmail.or_dvir.tracks.models

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class EventOccurrence(
    val startMillis: Long,
    val endMillis: Long?,
    val note: String?,
    // generated by room
    val id: Int = 0,
    // generated by room
    val eventId: Int
) {
    private companion object {
        // todo what does MEDIUM look like? should i change it?
        val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    }

    private val startDateTime =
        Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    private val startTimeUserFriendly = startDateTime.format(dateTimeFormatter)

    private val endDateTime = endMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
    private val endTimeUserFriendly = endDateTime?.format(dateTimeFormatter)
}
