package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.tracks.R
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.millisToLocalDate
import com.hotmail.or_dvir.tracks.millisToLocalTime
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.toEpochMillis
import com.hotmail.or_dvir.tracks.toUserFriendlyText
import com.hotmail.or_dvir.tracks.ui.DeleteConfirmationDialog
import com.hotmail.or_dvir.tracks.ui.SwipeToDelete
import com.hotmail.or_dvir.tracks.ui.TracksDialog
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnCreateNewOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnDeleteOccurrence
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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

        var showNewEditOccurrenceDialog by remember { mutableStateOf(false) }
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
                FloatingActionButton(onClick = { showNewEditOccurrenceDialog = true }) {
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

                if (showNewEditOccurrenceDialog) {
                    NewEditOccurrenceDialog(
                        onUserEvent = viewModel::onUserEvent,
                        onDismiss = { showNewEditOccurrenceDialog = false }
                    )
                }
            }
        }
    }

    @Composable
    private fun NewEditOccurrenceDialog(
        onUserEvent: OnUserEvent,
        onDismiss: () -> Unit
    ) {
        var startDateTime by remember { mutableStateOf(LocalDateTime.now()) }
//        var startMillis by remember { mutableStateOf(System.currentTimeMillis()) }
        var endMillis: Long? by remember { mutableStateOf(null) }
        var note: String by remember { mutableStateOf("") }
        val currentMillis = remember { System.currentTimeMillis() }

        TracksDialog(
            titleRes = R.string.dialogTitle_newOccurrence,
            positiveButtonRes = R.string.create,
            onDismiss = onDismiss,
            onPositiveButtonClick = {
                onUserEvent(
                    OnCreateNewOccurrence(
                        startMillis = startDateTime.toEpochMillis(),
//                        startMillis = startMillis,
                        endMillis = endMillis,
                        note = note.takeIf { it.isNotBlank() }
                    )
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // start date/time
                StartEndTimePicker(
                    preText = R.string.start,
                    dateText = currentMillis.millisToLocalDate().toUserFriendlyText(),
                    onDateChanged = {
                        startDateTime = LocalDateTime.of(it, startDateTime.toLocalTime())
                    },
                    timeText = currentMillis.millisToLocalTime().toUserFriendlyText(),
                    onTimeChanged = {
                        startDateTime = LocalDateTime.of(startDateTime.toLocalDate(), it)
                    }
                )

                // end date/time
                //todo

                // note
                // todo
                //  make outlined???
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(R.string.hint_note)) }
                )

                //todo
                //  start time millis
                //  end time millis - with option to remove
                //  note
            }
        }
    }

    @Composable
    private fun StartEndTimePicker(
        @StringRes preText: Int,
        dateTime: LocalDateTime,
        onDateChanged: (LocalDate) -> Unit,
        onTimeChanged: (LocalTime) -> Unit,
    ) {
        // todo
        //  option to remove date (but only for end)
        //  option to remove time (both for start/end)

        val datePickerState = rememberMaterialDialogState()






        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(preText))
            Spacer(Modifier.width(5.dp))

            // date
            Text(
                text = dateText,
                color = MaterialTheme.colors.secondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    // todo open date picker
                }
            )
            Spacer(Modifier.width(8.dp))
            // time
            Text(
                text = timeText,
                color = MaterialTheme.colors.secondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    // todo open time picker
                }
            )
        }

        //date picker
        MaterialDialog(
            dialogState = datePickerState,
            buttons = {
                positiveButton(res = R.string.select)
                negativeButton(res = R.string.cancel)
                //todo change text to "delete"?
                button(res = R.string.remove)
            },
        ) {
            datepicker(
                // todo
                //  set title?
                //  set initial date to currently selected date
                onDateChange = {
                    asdasadadsdadasdaad
                }
            )
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
                            showDeleteConfirmationDialog = Pair(true, userEvent.occurrenceId)
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
                    .padding(16.dp),
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
