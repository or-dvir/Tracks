package com.hotmail.or_dvir.tracks.ui.homeScreen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.tracks.R
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.ui.DeleteConfirmationDialog
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrenceScreen
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent
import com.hotmail.or_dvir.tracks.ui.homeScreen.HomeScreenViewModel.UserEvent.OnDeleteEvent

// todo
//  delete trackable event
//      also delete all instances of this event!!!

//todo
//  next steps:
//  * implement "Event occurrences" screen
//  * implement "occurrence details" screen
//      really necessary? can i integrate it into the "row"???

typealias OnUserEvent = (event: UserEvent) -> Unit

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getViewModel<HomeScreenViewModel>()
        var showNewEventDialog by remember { mutableStateOf(false) }

        Scaffold(
            //todo
            // do i want a top app bar here?
            // should the top app bar be shared for all screens?
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
                    NonEmptyContent(
                        trackedEvents = trackedEvents,
                        onUserEvent = viewModel::onUserEvent
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
        val dummyEvenId by remember { mutableStateOf(-1) }
        // first - should show dialog
        // second - event id to delete
        var showDeleteConfirmationDialog by remember { mutableStateOf(Pair(false, dummyEvenId)) }

        LazyColumn {
            itemsIndexed(
                items = trackedEvents,
                key = { _, item -> item.id },
            ) { index, item ->
                TrackedEventRow(
                    event = item,
                    onUserEvent = { event ->
                        if (event is OnDeleteEvent) {
                            showDeleteConfirmationDialog = Pair(true, event.id)
                        } else {
                            onUserEvent(event)
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
                onPositiveButtonClicked = { onUserEvent(OnDeleteEvent(second)) },
                onDismiss = { showDeleteConfirmationDialog = Pair(false, dummyEvenId) }
            )
        }
    }

    @Composable
    private fun NewEventDialog(
        onUserEvent: OnUserEvent,
        onDismiss: () -> Unit
    ) {
        var userInput by remember {
            mutableStateOf("")
        }

        var isError by remember {
            mutableStateOf(true)
        }

        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = RoundedCornerShape(5.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    //title
                    Text(
                        text = stringResource(R.string.dialogTitle_newTrackableEvent),
                        style = MaterialTheme.typography.h6
                    )

                    //body
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 16.dp,
                                bottom = 5.dp
                            )
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
                            Text(
                                text = stringResource(R.string.error_eventNameMustNotBeEmpty),
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 5.dp
                                )
                            )
                        }
                    }

                    //buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.cancel))
                        }

                        TextButton(onClick = {
                            if (!isError) {
                                onUserEvent(UserEvent.OnCreateNewEvent(userInput))
                                onDismiss()
                            }
                        }) {
                            Text(stringResource(R.string.create))
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    private fun LazyItemScope.TrackedEventRow(
        event: TrackedEvent,
        onUserEvent: OnUserEvent
    ) {
        val updatedEvent by rememberUpdatedState(event)
        val dismissState = rememberDismissState(
            confirmStateChange = {
                if (it == DismissValue.Default) {
                    //threshold has NOT been reached
                } else {
                    //threshold has been reached - item is dismissed
                    onUserEvent(OnDeleteEvent(updatedEvent.id))
                }

                //we are showing a deletion confirmation dialog in the caller composable.
                //until the user confirms the action, the row should NOT be dismissed
                false
            }
        )

        SwipeToDismiss(
            modifier = Modifier
                .fillMaxWidth()
                .padding()
                .animateItemPlacement(),
            dismissThresholds = { FractionalThreshold(0.5f) },
            state = dismissState,
            background = {
                //should help with performance
                dismissState.dismissDirection?.apply {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                            .padding(start = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null
                        )
                    }
                }
            },
            directions = setOf(DismissDirection.StartToEnd),
            dismissContent = {
                val navigator = LocalNavigator.currentOrThrow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                        .clickable {
                            navigator.push(EventOccurrenceScreen(event))
                        }
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(event.name)
                    IconButton(
                        onClick = { onUserEvent(UserEvent.OnQuickInstanceClicked(event.id)) }
                    ) {
                        Icon(
                            tint = MaterialTheme.colors.secondaryVariant,
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = stringResource(R.string.contentDescription_quickInstance)
                        )
                    }
                }
            }
        )
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
