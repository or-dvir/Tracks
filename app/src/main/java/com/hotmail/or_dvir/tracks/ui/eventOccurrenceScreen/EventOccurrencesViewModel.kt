package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.hotmail.or_dvir.tracks.database.repositories.EventOccurrencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

//@HiltViewModel
class EventOccurrencesViewModel @AssistedInject constructor(
    @Assisted
    private val eventId: Int,
    private val repo: EventOccurrencesRepository
) : ScreenModel {
//) : ViewModel() {

//    look into this:
//    https://github.com/adrielcafe/voyager/blob/main/samples/android/src/main/java/cafe/adriel/voyager/sample/hiltIntegration/HiltModule.kt
//    i want to inject repository from Hilt, and eventId from voyager library using ScreenModel!
//    according to https://voyager.adriel.cafe/screenmodel/hilt-integration,
//    hilt integration should work without annotating @HiltViewModel
//    does it just work????
//    if not, HOW?!?!?!?!??!
//    can i use saved handle state somehow???
//    https://github.com/adrielcafe/voyager/blob/main/samples/android/src/main/java/cafe/adriel/voyager/sample/hiltIntegration/HiltListViewModel.kt

    val eventOccurrencesFlow = repo.getAllByStartDateDesc(eventId)

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
