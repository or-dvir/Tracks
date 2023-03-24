package com.hotmail.or_dvir.tracks.ui.homeScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.tracks.R
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.ui.DeleteConfirmationDialog
import com.hotmail.or_dvir.tracks.ui.ErrorText
import com.hotmail.or_dvir.tracks.ui.SwipeToDelete
import com.hotmail.or_dvir.tracks.ui.TracksDialog
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrenceScreen
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteEvent

// todo
//  delete trackable event
//      also delete all instances of this event!!!

//todo
//  next steps:
//  * implement "rename" feature
//      should add ability to swipe in other direction (shared composable!),
//      and open the "new item" dialog with the current name filled in
//  * implement "Event occurrences" screen
//  * implement "occurrence details" screen
//      really necessary? can i integrate it into the "row"???

private typealias OnUserEvent = (event: UserEvent) -> Unit

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getViewModel<HomeScreenViewModel>()
        var showNewEventDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.homeScreen_title)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showNewEventDialog = true }) {
                    Icon(
                        contentDescription = stringResource(R.string.contentDescription_addTrackedEvent),
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
                val trackedEvents =
                    viewModel.trackedEventsFlow.collectAsStateLifecycleAware(initial = emptyList()).value

                if (trackedEvents.isEmpty()) {
                    EmptyContent()
                } else {
                    val context = LocalContext.current
                    NonEmptyContent(
                        trackedEvents = trackedEvents,
                        onUserEvent = { userEvent ->
                            if (userEvent is UserEvent.OnQuickOccurrenceClicked) {
                                //this is NOT a composable scope so this should NOT be a side effect
                                Toast.makeText(
                                    context,
                                    R.string.occurrenceAdded,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            viewModel.onUserEvent(userEvent)
                        }
                    )
                }

                if (showNewEventDialog) {
                    NewEventDialog(
                        onUserEvent = viewModel::onUserEvent,
                        onDismiss = { showNewEventDialog = false }
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
            Text(stringResource(R.string.homeScreen_emptyView))
        }
    }

    @Composable
    private fun NonEmptyContent(
        trackedEvents: List<TrackedEvent>,
        onUserEvent: OnUserEvent
    ) {
        val dummyEventId by remember { mutableStateOf(-1) }
        // first - should show dialog
        // second - event id to delete
        var showDeleteConfirmationDialog by remember { mutableStateOf(Pair(false, dummyEventId)) }

        LazyColumn {
            itemsIndexed(
                items = trackedEvents,
                key = { _, event -> event.id },
            ) { index, trackedEvent ->
                TrackedEventRow(
                    event = trackedEvent,
                    onUserEvent = { userEvent ->
                        if (userEvent is OnDeleteEvent) {
                            showDeleteConfirmationDialog = Pair(true, userEvent.eventId)
                        } else {
                            onUserEvent(userEvent)
                        }
                    }
                )

                if (index != trackedEvents.lastIndex) {
                    Divider()
                }
            }
        }

        showDeleteConfirmationDialog.takeIf { pair -> pair.first }?.apply {
            DeleteConfirmationDialog(
                messageRes = R.string.homeScreen_deleteConfirmation,
                onConfirm = { onUserEvent(OnDeleteEvent(second)) },
                onDismiss = { showDeleteConfirmationDialog = Pair(false, dummyEventId) }
            )
        }
    }

    @Composable
    private fun NewEventDialog(
        onUserEvent: OnUserEvent,
        onDismiss: () -> Unit
    ) {
        var userInput by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(true) }

        TracksDialog(
            titleRes = R.string.dialogTitle_newTrackableEvent,
            positiveButtonRes = R.string.create,
            positiveButtonEnabled = !isError,
            onDismiss = onDismiss,
            onPositiveButtonClick = {
                onUserEvent(UserEvent.OnCreateNewEvent(userInput))
                onDismiss()
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = userInput,
                    onValueChange = {
                        isError = it.isBlank()
                        userInput = it
                    },
                    placeholder = {
                        Text(stringResource(R.string.hint_eventName))
                    }
                )

                if (isError) {
                    ErrorText(R.string.error_eventNameMustNotBeEmpty)
                }
            }
        }
    }

    @Composable
    private fun LazyItemScope.TrackedEventRow(
        event: TrackedEvent,
        onUserEvent: OnUserEvent
    ) {
        val updatedEvent by rememberUpdatedState(event)

        SwipeToDelete(
            onDeleteRequest = { onUserEvent(OnDeleteEvent(updatedEvent.id)) },
            onEditRequest = {
                //todo
            }
        ) {
            val navigator = LocalNavigator.currentOrThrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .clickable { navigator.push(EventOccurrenceScreen(updatedEvent)) }
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(event.name)
                IconButton(
                    onClick = { onUserEvent(UserEvent.OnQuickOccurrenceClicked(updatedEvent.id)) }
                ) {
                    Icon(
                        tint = MaterialTheme.colors.secondaryVariant,
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = stringResource(R.string.contentDescription_quickInstance)
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun NewEventDialogPreview() {
        NewEventDialog(
            onUserEvent = {},
            onDismiss = {}
        )
    }

    @Preview(showBackground = true)
    @Composable
    private fun TrackedEventRowPreview() {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(1) {
                TrackedEventRow(
                    TrackedEvent(name = "event name"),
                    onUserEvent = { }
                )
            }
        }
    }
}
