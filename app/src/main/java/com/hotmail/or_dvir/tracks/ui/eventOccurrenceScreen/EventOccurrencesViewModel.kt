package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.tracks.database.repositories.EventOccurrencesRepository
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnCreateNewOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnDeleteOccurrence
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class EventOccurrencesViewModel @AssistedInject constructor(
    @Assisted
    private val eventId: Int,
    private val repo: EventOccurrencesRepository
) : ScreenModel {

    val eventOccurrencesFlow = repo.getAllByStartDateDesc(eventId)

    fun onUserEvent(event: UserEvent) {
        when (event) {
            is OnCreateNewOccurrence -> onCreateNewOccurrence()
            is OnDeleteOccurrence -> onDeleteOccurrence()

        }
    }

    private fun onCreateNewOccurrence() {
        // todo
    }

    private fun onDeleteOccurrence() {
        // todo
    }

    @dagger.assisted.AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(eventId: Int): EventOccurrencesViewModel
    }

    sealed class UserEvent {
        // todo what parameters do i need here???
        object OnCreateNewOccurrence : UserEvent()
        data class OnDeleteOccurrence(val id: Int) : UserEvent()
    }
}
