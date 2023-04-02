package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.tracks.database.repositories.EventOccurrencesRepository
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnDeleteOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnNewOrEditOccurrence
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class EventOccurrencesViewModel @AssistedInject constructor(
    @Assisted
    private val eventId: Int,
    private val repo: EventOccurrencesRepository
) : ScreenModel {

    // todo
    //  add sticky header to list for each year/month?

    val eventOccurrencesFlow = repo.getAllByStartDateDesc(eventId)

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnNewOrEditOccurrence -> onNewOrEditOccurrence(userEvent.occurrence)
            is OnDeleteOccurrence -> onDeleteOccurrence(userEvent.occurrenceId)
        }
    }

    private fun onNewOrEditOccurrence(occurrence: EventOccurrence) =
        coroutineScope.launch { repo.insertOrReplace(occurrence) }

    private fun onDeleteOccurrence(occurrenceId: Int) =
        coroutineScope.launch { repo.delete(occurrenceId) }

    @dagger.assisted.AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(eventId: Int): EventOccurrencesViewModel
    }

    sealed class UserEvent {
        data class OnNewOrEditOccurrence(val occurrence: EventOccurrence) : UserEvent()
        data class OnDeleteOccurrence(val occurrenceId: Int) : UserEvent()
    }
}
