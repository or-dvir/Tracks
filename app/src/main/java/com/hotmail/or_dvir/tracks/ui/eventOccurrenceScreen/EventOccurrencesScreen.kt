package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.tracks.R
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.models.EventOccurrence
import com.hotmail.or_dvir.tracks.models.TrackedEvent
import com.hotmail.or_dvir.tracks.toUserFriendlyText
import com.hotmail.or_dvir.tracks.ui.DeleteConfirmationDialog
import com.hotmail.or_dvir.tracks.ui.SwipeToDelete
import com.hotmail.or_dvir.tracks.ui.TracksDialog
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.EventOccurrenceData
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnCreateNewOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnDeleteOccurrence
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
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
                    val context = LocalContext.current
                    NewEditOccurrenceDialog(
                        onUserEvent = { userEvent ->
                            if (userEvent is OnCreateNewOccurrence) {
                                Toast.makeText(
                                    context,
                                    R.string.occurrenceAdded,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            viewModel.onUserEvent(userEvent)
                        },
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
        var startDate: LocalDate by remember { mutableStateOf(LocalDate.now()) }
        var startTime: LocalTime? by remember { mutableStateOf(null) }
        var endDate: LocalDate? by remember { mutableStateOf(null) }
        var endTime: LocalTime? by remember { mutableStateOf(null) }
        var note: String by remember { mutableStateOf("") }

        TracksDialog(
            titleRes = R.string.dialogTitle_newOccurrence,
            positiveButtonRes = R.string.create,
            onDismiss = onDismiss,
            onPositiveButtonClick = {
                onUserEvent(
                    OnCreateNewOccurrence(
                        EventOccurrenceData(
                            startDate = startDate,
                            startTime = startTime,
                            endDate = endDate,
                            endTime = endTime,
                            note = note
                        )
                    )
                )

                onDismiss()
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // start date/time
                StartEndDateTimeRow(
                    preText = R.string.preText_start,
                    // an occurrence must at least have a start date
                    removableStartDate = false,
                    selectedDate = startDate,
                    selectedTime = startTime,
                    onDateChanged = {
                        //since we set removableStartDate to `false`, `it` should not be null here
                        startDate = it!!
                    },
                    onTimeChanged = { startTime = it },
                )

                // end date/time
                StartEndDateTimeRow(
                    preText = R.string.preText_end,
                    selectedDate = endDate,
                    selectedTime = endTime,
                    onDateChanged = { endDate = it },
                    onTimeChanged = { endTime = it },
                )

                // note
                // todo
                //  make outlined???
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(R.string.hint_note)) }
                )
            }
        }
    }

    @Composable
    private fun StartEndDateTimeRow(
        @StringRes preText: Int,
        selectedDate: LocalDate?,
        selectedTime: LocalTime?,
        onDateChanged: (LocalDate?) -> Unit,
        onTimeChanged: (LocalTime?) -> Unit,
        removableStartDate: Boolean = true
    ) {
        // todo
        //  do not allow to pick end date BEFORE start date
        //  do not allow to pick end time BEFORE start time
        val datePickerState = rememberMaterialDialogState()
        val timePickerState = rememberMaterialDialogState()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(preText))
            Spacer(Modifier.width(5.dp))

            //date
            Text(
                text = selectedDate?.toUserFriendlyText() ?: stringResource(R.string.setDate),
                color = MaterialTheme.colors.secondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { datePickerState.show() }
            )
            Spacer(Modifier.width(8.dp))

            //time
            Text(
                text = selectedTime?.toUserFriendlyText() ?: stringResource(R.string.setTime),
                color = MaterialTheme.colors.secondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { timePickerState.show() }
            )
        }

        //date picker
        MaterialDialog(
            dialogState = datePickerState,
            buttons = {
                positiveButton(res = R.string.set)
                negativeButton(res = R.string.cancel)
                if (removableStartDate) {
                    //todo
                    // change text to "delete"?
                    // button is in the middle, how can i move to end?
                    button(
                        res = R.string.remove,
                        onClick = {
                            onDateChanged(null)
                            datePickerState.hide()
                        }
                    )
                }
            },
        ) {
            datepicker(
                title = "",
                onDateChange = onDateChanged,
                initialDate = selectedDate ?: LocalDate.now()
            )
        }

        //time picker
        MaterialDialog(
            dialogState = timePickerState,
            buttons = {
                positiveButton(res = R.string.set)
                negativeButton(res = R.string.cancel)
                //todo
                // change text to "delete"?
                // button is in the middle, how can i move to end?
                button(
                    res = R.string.remove,
                    onClick = {
                        onTimeChanged(null)
                        timePickerState.hide()
                    }
                )
            },
        ) {
            timepicker(
                is24HourClock = true,
                title = "",
                onTimeChange = onTimeChanged,
                initialTime = selectedTime ?: LocalTime.now()
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp),
                // todo do i need this?
//                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    occurrence.apply {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(startDate.toUserFriendlyText())
                            startTime?.let { Text(it.toUserFriendlyText()) }
                        }

                        if(listOf<Any?>(endDate, endTime).any { it != null }) {
                            Text(stringResource(R.string.until))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                endDate?.let { Text(it.toUserFriendlyText()) }
                                endTime?.let { Text(it.toUserFriendlyText()) }
                            }
                        }
                    }
                }
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
