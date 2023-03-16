package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.tracks.R
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnCreateNewOccurrence

data class EventOccurrenceScreen(val event: TrackedEvent) : Screen {
    @Composable
    override fun Content() {
        val viewModel =
            getScreenModel<EventOccurrencesViewModel, EventOccurrencesViewModel.Factory> {
                it.create(event.id)
            }

        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(
                                contentDescription = stringResource(R.string.contentDescription_back),
                                imageVector = Icons.Filled.ArrowBack
                            )
                        }
                    },
                    title = { Text(event.name) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.onUserEvent(OnCreateNewOccurrence) }
                ) {
                    Icon(
                        contentDescription = stringResource(R.string.contentDescription_addEventOccurrence),
                        imageVector = Icons.Filled.Add
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //todo what are the "contentValues"? do i need this???
                    .padding(it)
            ) {
                val eventOccurrences =
                    viewModel.eventOccurrencesFlow.collectAsStateLifecycleAware(initial = emptyList()).value

                if (eventOccurrences.isEmpty()) {
                    EmptyContent()
                } else {
                    NonEmptyContent(eventOccurrences = eventOccurrences)
                }
            }
        }
    }

    @Composable
    private fun EmptyContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.eventOccurrences_emptyView))
        }
    }

    @Composable
    private fun NonEmptyContent(eventOccurrences: List<EventOccurrence>) {
        // todo
        //  action bar title
        //  FAB to add new occurrence
        //  swipe to delete with confirmation dialog
    }

    @Composable
    private fun EventOccurrenceRow() {
        // todo
        //  start date
        //      edit feature
        //  end date
        //      edit feature
        //  note
        //      edit feature
    }
}
