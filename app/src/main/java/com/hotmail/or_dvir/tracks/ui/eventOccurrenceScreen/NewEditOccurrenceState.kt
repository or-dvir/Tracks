package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hotmail.or_dvir.tracks.isBefore
import java.time.LocalDate
import java.time.LocalTime

class NewEditOccurrenceState {

    var startDate: LocalDate by mutableStateOf(LocalDate.now())
    var endDate: LocalDate? by mutableStateOf(null)
    val areStartEndSameDay by derivedStateOf { endDate?.isEqual(startDate) == true }

    var startTime: LocalTime? by mutableStateOf(null)
    var endTime: LocalTime? by mutableStateOf(null)
    var note: String by mutableStateOf("")

    //loophole: if the user selects an endTime before an endDate, there can be a scenario
    //where the end date/time will be BEFORE the start date/time
    val errorEndTimeBeforeStartTime by derivedStateOf {
        areStartEndSameDay && endTime.isBefore(startTime)
    }
}