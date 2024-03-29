package com.hotmail.or_dvir.tracks.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotmail.or_dvir.tracks.database.repositories.EventOccurrencesRepository
import com.hotmail.or_dvir.tracks.database.repositories.TrackedEventsRepository
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnCreateNewEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnEditEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnQuickOccurrenceClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val trackedEventsRepo: TrackedEventsRepository,
    private val eventOccurrencesRepo: EventOccurrencesRepository
) : ViewModel() {

    val trackedEventsFlow = trackedEventsRepo.getAllSortedByAlphabet()

    fun onUserEvent(userEvent: UserEvent) {
        when (userEvent) {
            is OnQuickOccurrenceClicked -> onQuickOccurrenceClicked(userEvent.eventId)
            is OnCreateNewEvent -> onCreateNewEvent(userEvent.name)
            is OnDeleteEvent -> onDeleteEvent(userEvent.eventId)
            is OnEditEvent -> onEditEvent(userEvent)
        }
    }

    private fun onEditEvent(userEvent: OnEditEvent) {
        viewModelScope.launch {
            trackedEventsRepo.update(
                TrackedEvent(
                    name = userEvent.eventName,
                    id = userEvent.eventId
                )
            )
        }
    }

    private fun onCreateNewEvent(name: String) {
        viewModelScope.launch {
            trackedEventsRepo.insertOrReplace(
                TrackedEvent(name = name)
            )
        }
    }

    private fun onDeleteEvent(eventId: Int) =
        viewModelScope.launch { trackedEventsRepo.delete(eventId) }

    private fun onQuickOccurrenceClicked(eventId: Int) {
        viewModelScope.launch {
            eventOccurrencesRepo.insertOrReplace(
                EventOccurrence(
                    note = "",
                    eventId = eventId,
                    startDate = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endDate = null,
                    endTime = null,
                )
            )
        }
    }

    sealed class UserEvent {
        data class OnCreateNewEvent(val name: String) : UserEvent()
        data class OnQuickOccurrenceClicked(val eventId: Int) : UserEvent()
        data class OnDeleteEvent(val eventId: Int) : UserEvent()
        data class OnEditEvent(val eventId: Int, val eventName: String) : UserEvent()
    }
}
