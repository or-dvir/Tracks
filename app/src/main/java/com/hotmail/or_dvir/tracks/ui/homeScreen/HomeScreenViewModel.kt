package com.hotmail.or_dvir.tracks.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotmail.or_dvir.tracks.database.repositories.TrackedEventsRepository
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.UserEvent.OnCreateNewEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.UserEvent.OnDeleteEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.UserEvent.OnEventClicked
import com.hotmail.or_dvir.tracks.ui.homeScreen.UserEvent.OnQuickAddClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repo: TrackedEventsRepository,
) : ViewModel() {

    val trackedEventsFlow = repo.getAllSortedByAlphabet()

    fun onUserEvent(event: UserEvent) {
        when (event) {
            is OnEventClicked -> onEventClicked(event.id)
            is OnQuickAddClicked -> onQuickAddClicked(event.id)
            is OnCreateNewEvent -> onCreateNewEvent(event.name)
            is OnDeleteEvent -> onDeleteEvent(event.id)
        }
    }

    private fun onCreateNewEvent(name: String) {
        viewModelScope.launch {
            repo.insert(
                TrackedEvent(name = name)
            )
        }
    }

    private fun onDeleteEvent(eventId: Int) {
        //todo
    }

    private fun onQuickAddClicked(eventId: Int) {
        //todo
    }

    private fun onEventClicked(eventId: Int) {
        // todo go to event instances screen
        //  should probably be directly in compose screen and not here...
    }

    // todo
    //  create new event
    //  create quick instance of event
    //  delete event
    //      also all of it's instances!!! make note for future!!!
}

sealed class UserEvent {
    data class OnCreateNewEvent(val name: String) : UserEvent()
    data class OnEventClicked(val id: Int) : UserEvent()
    data class OnQuickAddClicked(val id: Int) : UserEvent()
    data class OnDeleteEvent(val id: Int) : UserEvent()
}