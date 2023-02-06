package com.hotmail.or_dvir.tracks.ui.homeScreen

import androidx.lifecycle.ViewModel
import com.hotmail.or_dvir.tracks.database.repositories.TrackedEventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    repo: TrackedEventsRepository,
) : ViewModel() {

    val trackedEventsFlow = repo.getAllSortedByAlphabet()

    // todo
    //  add event
    //  delete event
    //      also all of it's instances!!! make note for future!!!
}
