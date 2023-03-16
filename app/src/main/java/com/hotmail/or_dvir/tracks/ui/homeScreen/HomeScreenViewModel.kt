package com.hotmail.or_dvir.tracks.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotmail.or_dvir.tracks.database.repositories.TrackedEventsRepository
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnCreateNewEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnEventClicked
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnQuickInstanceClicked
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
            is OnQuickInstanceClicked -> onQuickInstanceClicked(event.id)
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
        viewModelScope.launch {
            repo.delete(eventId)
        }
    }

    private fun onQuickInstanceClicked(eventId: Int) {
        //todo
    }

    private fun onEventClicked(eventId: Int) {
        // todo go to event occurrences screen
        //  should probably be directly in compose screen and not here...
    }

    // todo
    //  create quick instance of event
    //  delete event
    //      also all of it's instances!!! make note for future!!!

    sealed class UserEvent {
        data class OnCreateNewEvent(val name: String) : UserEvent()
        data class OnEventClicked(val id: Int) : UserEvent()
        data class OnQuickInstanceClicked(val id: Int) : UserEvent()
        data class OnDeleteEvent(val id: Int) : UserEvent()
    }
}
