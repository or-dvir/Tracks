package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.tracks.database.repositories.EventOccurrencesRepository
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnCreateNewOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnDeleteOccurrence
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.launch

class EventOccurrencesViewModel @AssistedInject constructor(
    @Assisted
    private val eventId: Int,
    private val repo: EventOccurrencesRepository
) : ScreenModel {

    // todo
    //  sort occurrences by date?
    //  add sticky header to list for each year/month?

    val eventOccurrencesFlow = repo.getAllByStartDateDesc(eventId)

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnCreateNewOccurrence -> onCreateNewOccurrence(userEvent.occurrenceData)
            is OnDeleteOccurrence -> onDeleteOccurrence(userEvent.occurrenceId)
        }
    }

    private fun onCreateNewOccurrence(data: EventOccurrenceData) {
        coroutineScope.launch {
            data.apply {
                repo.insert(
                    EventOccurrence(
                        startDate = startDate,
                        startTime = startTime,
                        endDate = endDate,
                        endTime = endTime,
                        note = note,
                        eventId = eventId
                    )
                )
            }
        }
    }

    private fun onDeleteOccurrence(occurrenceId: Int) =
        coroutineScope.launch { repo.delete(occurrenceId) }

    @dagger.assisted.AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(eventId: Int): EventOccurrencesViewModel
    }

    data class EventOccurrenceData(
        val startDate: LocalDate,
        val startTime: LocalTime?,
        val endDate: LocalDate?,
        val endTime: LocalTime?,
        val note: String
    )

    sealed class UserEvent {
        data class OnCreateNewOccurrence(val occurrenceData: EventOccurrenceData) : UserEvent()
        data class OnDeleteOccurrence(val occurrenceId: Int) : UserEvent()
    }
}
