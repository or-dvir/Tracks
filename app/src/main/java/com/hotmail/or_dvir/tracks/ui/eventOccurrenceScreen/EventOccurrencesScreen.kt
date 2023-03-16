package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.tracks.R
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.ui.DeleteConfirmationDialog
import com.hotmail.or_dvir.tracks.ui.SwipeToDelete
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnCreateNewOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnDeleteOccurrence

private typealias OnUserEvent = (event: UserEvent) -> Unit

data class EventOccurrenceScreen(val event: TrackedEvent) : Screen {

    // required for previews
//    private constructor() : this(
//        TrackedEvent(name = "Luke", id = 0)
//    )

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
                    NonEmptyContent(
                        eventOccurrences = eventOccurrences,
                        onUserEvent = viewModel::onUserEvent
                    )
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
            Text(stringResource(R.string.eventOccurrencesScreen_emptyView))
        }
    }

    @Composable
    private fun NonEmptyContent(
        eventOccurrences: List<EventOccurrence>,
        onUserEvent: OnUserEvent
    ) {
        // todo
        //  FAB to add new occurrence
        //  swipe to delete with confirmation dialog

        val dummyOccurrenceId by remember { mutableStateOf(-1) }
        // first - should show dialog
        // second - event id to delete
        var showDeleteConfirmationDialog by remember {
            mutableStateOf(Pair(false, dummyOccurrenceId))
        }

        LazyColumn {
            itemsIndexed(
                items = eventOccurrences,
                key = { _, occurrence -> occurrence.id },
            ) { index, occurrence ->
                EventOccurrenceRow(
                    occurrence = occurrence,
                    onUserEvent = { userEvent ->
                        if (userEvent is OnDeleteOccurrence) {
                            showDeleteConfirmationDialog = Pair(true, userEvent.id)
                        } else {
                            onUserEvent(userEvent)
                        }
                    }
                )

                if (index != eventOccurrences.lastIndex) {
                    Divider()
                }
            }
        }

        showDeleteConfirmationDialog.takeIf { pair -> pair.first }?.apply {
            DeleteConfirmationDialog(
                messageRes = R.string.eventOccurrencesScreen_deleteConfirmation,
                onConfirm = { onUserEvent(OnDeleteOccurrence(second)) },
                onDismiss = { showDeleteConfirmationDialog = Pair(false, dummyOccurrenceId) }
            )
        }
    }

    @Composable
    private fun LazyItemScope.EventOccurrenceRow(
        occurrence: EventOccurrence,
        onUserEvent: OnUserEvent
    ) {
        // todo
        //  start date
        //      edit feature
        //  end date
        //      edit feature
        //  note
        //      edit feature

        val updatedOccurrence by rememberUpdatedState(occurrence)

        SwipeToDelete(
            onDeleteRequested = { onUserEvent(OnDeleteOccurrence(updatedOccurrence.id)) },
        ) {
            val navigator = LocalNavigator.currentOrThrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(8.dp),
                // todo do i need this?
//                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(occurrence.startTimeUserFriendly)
            }
        }
    }

    //todo why aren't previews working?!
//    @Preview(showBackground = true)
//    @Composable
//    private fun TrackedEventRowPreview() {
//        LazyColumn(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(1) {
//                EventOccurrenceRow(
//                    onUserEvent = { },
//                    occurrence = EventOccurrence(
//                        startMillis = System.currentTimeMillis(),
//                        endMillis = null,
//                        note = "my note",
//                        id = 0,
//                        eventId = 0
//                    )
//                )
//            }
//        }
//    }
}
