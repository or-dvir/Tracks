package com.hotmail.or_dvir.tracks.ui.homeScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.tracks.R
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.lazyListLastItemSpacer
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.ui.DeleteConfirmationDialog
import com.hotmail.or_dvir.tracks.ui.ErrorText
import com.hotmail.or_dvir.tracks.ui.SwipeToDeleteOrEdit
import com.hotmail.or_dvir.tracks.ui.TracksDialog
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrenceScreen
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnEditEvent
import com.hotmail.or_dvir.tracks.ui.mainActivity.MainActivityViewModel
import com.hotmail.or_dvir.tracks.ui.rememberDeleteConfirmationDialogState

//todo
//  app icon - credit to Icongeek26 from flaticon

private typealias OnUserEvent = (event: UserEvent) -> Unit

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getViewModel<HomeScreenViewModel>()
        val newEventDialogState = rememberNewEditDialogState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.homeScreen_title)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { newEventDialogState.show = true }) {
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

                newEventDialogState.apply {
                    NewEditEventDialog(
                        state = this,
                        onConfirm = {
                            viewModel.onUserEvent(
                                UserEvent.OnCreateNewEvent(newEventDialogState.userInput)
                            )
                        },
                        onDismiss = { reset() }
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
        val deleteConfirmationState = rememberDeleteConfirmationDialogState()
        val editEventState = rememberNewEditDialogState()

        LazyColumn {
            itemsIndexed(
                items = trackedEvents,
                key = { _, event -> event.id },
            ) { index, trackedEvent ->
                TrackedEventRow(
                    event = trackedEvent,
                    onUserEvent = { userEvent ->
                        when (userEvent) {
                            is OnDeleteEvent -> deleteConfirmationState.apply {
                                objToDeleteId = userEvent.eventId
                                show = true
                            }
                            is OnEditEvent -> editEventState.apply {
                                userInput = userEvent.eventName
                                editedEventId = userEvent.eventId
                                show = true
                            }
                            else -> onUserEvent(userEvent)
                        }
                    }
                )

                if (index == trackedEvents.lastIndex) {
                    Spacer(modifier = Modifier.height(lazyListLastItemSpacer))
                } else {
                    Divider()
                }
            }
        }

        deleteConfirmationState.apply {
            DeleteConfirmationDialog(
                state = this,
                messageRes = R.string.homeScreen_deleteConfirmation,
                onConfirm = { onUserEvent(OnDeleteEvent(objToDeleteId)) },
                onDismiss = { reset() }
            )
        }

        editEventState.apply {
            NewEditEventDialog(
                state = this,
                onConfirm = {
                    editedEventId?.let { onUserEvent(OnEditEvent(it, userInput)) }
                },
                onDismiss = { reset() }
            )
        }
    }

    @Composable
    private fun rememberNewEditDialogState() = remember { NewEditEventDialogState() }

    @Composable
    private fun NewEditEventDialog(
        state: NewEditEventDialogState,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (!state.show) {
            return
        }

        val isEditing by remember(state.editedEventId) {
            mutableStateOf(state.editedEventId != null)
        }

        TracksDialog(
            titleRes = if (isEditing) R.string.dialogTitle_editTrackableEvent else R.string.dialogTitle_newTrackableEvent,
            positiveButtonRes = if (isEditing) R.string.edit else R.string.create,
            positiveButtonEnabled = !state.isError,
            onDismiss = onDismiss,
            onPositiveButtonClick = {
                onConfirm()
                onDismiss()
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = state.userInput,
                    onValueChange = { state.userInput = it },
                    placeholder = {
                        Text(stringResource(R.string.hint_eventName))
                    }
                )

                if (state.isError) {
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

        SwipeToDeleteOrEdit(
            onDeleteRequest = { onUserEvent(OnDeleteEvent(updatedEvent.id)) },
            onEditRequest = { onUserEvent(OnEditEvent(updatedEvent.id, updatedEvent.name)) }
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
}
