package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.tracks.database.repositories.EventOccurrencesRepository
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnCreateNewOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnDeleteOccurrence
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
            is OnCreateNewOccurrence -> onCreateNewOccurrence()
            is OnDeleteOccurrence -> onDeleteOccurrence(userEvent.occurrenceId)
        }
    }

    private fun onCreateNewOccurrence() {
        //todo
//        coroutineScope.launch {
//            repo.insert(
//                EventOccurrence(
//                    startMillis =,
//                    endMillis =,
//                    note =,
//                    eventId = eventId
//                )
//            )
//        }
    }

    private fun onDeleteOccurrence(occurrenceId: Int) =
        coroutineScope.launch { repo.delete(occurrenceId) }

    @dagger.assisted.AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(eventId: Int): EventOccurrencesViewModel
    }

    sealed class UserEvent {
        // todo what parameters do i need here???
        object OnCreateNewOccurrence : UserEvent()
        data class OnDeleteOccurrence(val occurrenceId: Int) : UserEvent()
    }
}
