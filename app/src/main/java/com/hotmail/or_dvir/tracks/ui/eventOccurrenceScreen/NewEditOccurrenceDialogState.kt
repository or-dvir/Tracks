package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hotmail.or_dvir.tracks.isBefore
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import java.time.LocalDate
import java.time.LocalTime

class NewEditOccurrenceDialogState {
    var show by mutableStateOf(DEFAULT_SHOW)
    var editedOccurrenceId: Int? by mutableStateOf(DEFAULT_OCCURRENCE_ID)

    var startDate: LocalDate by mutableStateOf(DEFAULT_START_DATE)
    var endDate: LocalDate? by mutableStateOf(DEFAULT_END_DATE)
    val areStartEndSameDay by derivedStateOf { endDate?.isEqual(startDate) == true }

    var startTime: LocalTime? by mutableStateOf(DEFAULT_START_TIME)
    var endTime: LocalTime? by mutableStateOf(DEFAULT_END_TIME)
    var note: String by mutableStateOf(DEFAULT_NOTE)

    //loophole: if the user selects an endTime before an endDate, there can be a scenario
    //where the end date/time will be BEFORE the start date/time
    val errorEndTimeBeforeStartTime by derivedStateOf {
        areStartEndSameDay && endTime.isBefore(startTime)
    }

    fun setFromOccurrence(occurrence: EventOccurrence) {
        occurrence.let {
            startDate = it.startDate
            endDate = it.endDate
            startTime = it.startTime
            endTime = it.endTime
            note = it.note
            editedOccurrenceId = it.id
        }
    }

    fun reset() {
        show = DEFAULT_SHOW
        editedOccurrenceId = DEFAULT_OCCURRENCE_ID
        note = DEFAULT_NOTE
        startDate = DEFAULT_START_DATE
        endDate = DEFAULT_END_DATE
        startTime = DEFAULT_START_TIME
        endTime = DEFAULT_END_TIME
    }

    private companion object {
        const val DEFAULT_SHOW = false
        const val DEFAULT_NOTE = ""
        val DEFAULT_OCCURRENCE_ID: Int? = null

        val DEFAULT_START_DATE: LocalDate = LocalDate.now()
        val DEFAULT_END_DATE: LocalDate? = null

        val DEFAULT_START_TIME: LocalTime? = null
        val DEFAULT_END_TIME: LocalTime? = null
    }
}
