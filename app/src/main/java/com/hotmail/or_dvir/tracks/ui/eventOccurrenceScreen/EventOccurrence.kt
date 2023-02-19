package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel

// todo navigate here when event is clicked
class EventOccurrenceScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getViewModel<EventOccurrenceViewModel>()


    }


}
