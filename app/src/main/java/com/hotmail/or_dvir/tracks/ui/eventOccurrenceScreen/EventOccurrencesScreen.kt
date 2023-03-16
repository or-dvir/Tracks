package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import android.util.Log
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel

data class EventOccurrenceScreen(val event: TrackedEvent) : Screen {
    @Composable
    override fun Content() {
        val viewModel =
            getScreenModel<EventOccurrencesViewModel, EventOccurrencesViewModel.Factory> {
                it.create(eventId)
            }

//        add empty view
    }
}
