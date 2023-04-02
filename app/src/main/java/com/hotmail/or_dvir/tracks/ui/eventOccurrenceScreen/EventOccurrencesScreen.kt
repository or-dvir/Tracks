package com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import com.hotmail.or_dvir.tracks.ui.ErrorText
import com.hotmail.or_dvir.tracks.ui.SwipeToDeleteOrEdit
import com.hotmail.or_dvir.tracks.ui.TracksDialog
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnDeleteOccurrence
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel.UserEvent.OnNewOrEditOccurrence
import com.hotmail.or_dvir.tracks.ui.rememberDeleteConfirmationDialogState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

private typealias OnUserEvent = (event: UserEvent) -> Unit

data class EventOccurrenceScreen(val event: TrackedEvent) : Screen {
    // todo
    //  animate placement of list items e.g. when renaming (also for home screen!!!)
    //  when scrolling all the way down, hide FAB (also home screen!!!)
    //  dark mode!!!
    //  change process name (fully qualified app name)

    @Composable
    override fun Content() {
        val viewModel =
            getScreenModel<EventOccurrencesViewModel, EventOccurrencesViewModel.Factory> {
                it.create(event.id)
            }

        val newOccurrenceDialogState = rememberNewEditOccurrenceState()
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
                FloatingActionButton(onClick = { newOccurrenceDialogState.show = true }) {
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

                newOccurrenceDialogState.apply {
                    val context = LocalContext.current
                    NewEditOccurrenceDialog(
                        state = this,
                        onConfirm = {
                            viewModel.onUserEvent(
                                OnNewOrEditOccurrence(
                                    EventOccurrence(
                                        startDate = startDate,
                                        endDate = endDate,
                                        startTime = startTime,
                                        endTime = endTime,
                                        note = note,
                                        eventId = event.id
                                    )
                                )
                            )

                            Toast.makeText(
                                context,
                                R.string.occurrenceAdded,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDismiss = { reset() }
                    )
                }
            }
        }
    }

    @Composable
    private fun rememberNewEditOccurrenceState() = remember { NewEditOccurrenceDialogState() }

    @Composable
    private fun NewEditOccurrenceDialog(
        state: NewEditOccurrenceDialogState,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (!state.show) {
            return
        }

        val isEditing by remember(state.editedOccurrenceId) {
            mutableStateOf(state.editedOccurrenceId != null)
        }

        state.apply {
            TracksDialog(
                titleRes = if(isEditing) R.string.dialogTitle_newOccurrence else R.string.dialogTitle_newOccurrence,
                positiveButtonRes = if(isEditing) R.string.edit else R.string.create,
                onDismiss = onDismiss,
                positiveButtonEnabled = !errorEndTimeBeforeStartTime,
                onPositiveButtonClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectableStartTimeRange by remember {
                        derivedStateOf {
                            val maxStartTime = if (!areStartEndSameDay) {
                                LocalTime.MAX
                            } else {
                                endTime ?: LocalTime.MAX
                            }

                            LocalTime.MIN..maxStartTime
                        }
                    }

                    val selectableEndTimeRange by remember {
                        derivedStateOf {
                            val minEndTime = if (!areStartEndSameDay) {
                                LocalTime.MIN
                            } else {
                                startTime ?: LocalTime.MIN
                            }

                            minEndTime..LocalTime.MAX
                        }
                    }

                    // todo align the dates text to the start of the longest word "start:" or "end:"
                    //  because of the error text, looks like the only way to do this is to use
                    //  constraint layout... do it later
                    // start date/time
                    StartEndDateTimeRow(
                        preText = R.string.preText_start,
                        // an occurrence must at least have a start date
                        removableStartDate = false,
                        selectedDate = startDate,
                        minSelectableDate = LocalDate.MIN,
                        maxSelectableDate = endDate ?: LocalDate.MAX,
                        selectedTime = startTime,
                        selectableTimeRange = selectableStartTimeRange,
                        onTimeChanged = { startTime = it },
                        onDateChanged = {
                            //since we set removableStartDate to `false`, `it` should not be null here
                            startDate = it!!
                        }
                    )

                    // end date/time. wrapper in extra Column so that the error appears right beneath it
                    //(ignores the "spacedBy" of the containing column
                    Column {
                        StartEndDateTimeRow(
                            preText = R.string.preText_end,
                            selectedDate = endDate,
                            minSelectableDate = startDate,
                            maxSelectableDate = LocalDate.MAX,
                            selectedTime = endTime,
                            selectableTimeRange = selectableEndTimeRange,
                            onDateChanged = { endDate = it },
                            onTimeChanged = { endTime = it }
                        )

                        if (errorEndTimeBeforeStartTime) {
                            ErrorText(R.string.error_endTimeBeforeStartTime)
                        }
                    }

                    // note
                    // todo make outlined???
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 290.dp),
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(stringResource(R.string.hint_note)) }
                    )
                }
            }
        }
    }

    @Composable
    private fun StartEndDateTimeRow(
        @StringRes preText: Int,
        selectedDate: LocalDate?,
        minSelectableDate: LocalDate,
        maxSelectableDate: LocalDate,
        selectedTime: LocalTime?,
        selectableTimeRange: ClosedRange<LocalTime>,
        onDateChanged: (LocalDate?) -> Unit,
        onTimeChanged: (LocalTime?) -> Unit,
        removableStartDate: Boolean = true
    ) {
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
            val selectableDates by remember {
                derivedStateOf { minSelectableDate..maxSelectableDate }
            }

            datepicker(
                title = "",
                onDateChange = onDateChanged,
                initialDate = selectedDate ?: LocalDate.now(),
                allowedDateValidator = { selectableDates.contains(it) }
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
                timeRange = selectableTimeRange,
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
        val deleteOccurrenceState = rememberDeleteConfirmationDialogState()
        val editOccurrenceState = rememberNewEditOccurrenceState()

        LazyColumn {
            itemsIndexed(
                items = eventOccurrences,
                key = { _, occurrence -> occurrence.id },
            ) { index, occurrence ->
                EventOccurrenceRow(
                    occurrence = occurrence,
                    onUserEvent = { userEvent ->
                        when (userEvent) {
                            is OnDeleteOccurrence -> deleteOccurrenceState.apply {
                                objToDeleteId = userEvent.occurrenceId
                                show = true
                            }
                            is OnNewOrEditOccurrence -> editOccurrenceState.apply {
                                setFromOccurrence(userEvent.occurrence)
                                show = true
                            }
                        }
                    }
                )

                if (index != eventOccurrences.lastIndex) {
                    Divider()
                }
            }
        }

        deleteOccurrenceState.apply {
            DeleteConfirmationDialog(
                state = this,
                messageRes = R.string.eventOccurrencesScreen_deleteConfirmation,
                onConfirm = { onUserEvent(OnDeleteOccurrence(objToDeleteId)) },
                onDismiss = { reset() }
            )
        }

        editOccurrenceState.apply {
            NewEditOccurrenceDialog(
                state = this,
                onDismiss = { reset() },
                onConfirm = {
                    editedOccurrenceId?.let {
                        onUserEvent(
                            OnNewOrEditOccurrence(
                                EventOccurrence(
                                    startDate = startDate,
                                    endDate = endDate,
                                    startTime = startTime,
                                    endTime = endTime,
                                    note = note,
                                    eventId = event.id,
                                    id = it
                                )
                            )
                        )
                    }
                }
            )
        }
    }

    @Composable
    private fun LazyItemScope.EventOccurrenceRow(
        occurrence: EventOccurrence,
        onUserEvent: OnUserEvent
    ) {
        val updatedOccurrence by rememberUpdatedState(occurrence)

        SwipeToDeleteOrEdit(
            onDeleteRequest = { onUserEvent(OnDeleteOccurrence(updatedOccurrence.id)) },
            onEditRequest = { onUserEvent(OnNewOrEditOccurrence(updatedOccurrence)) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        if (occurrence.hasEndDateTime) {
                            OccurrenceColumnFromUntil()
                        }

                        OccurrenceColumnStartEndDateTime(occurrence)
                    }

                    occurrence.note.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            modifier = Modifier
                                .heightIn(max = 175.dp)
                                .padding(start = 8.dp)
                                .verticalScroll(rememberScrollState()),
                            text = it,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun OccurrenceColumnStartEndDateTime(occurrence: EventOccurrence) {
        occurrence.apply {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(startDate.toUserFriendlyText())
                    startTime?.let { Text(it.toUserFriendlyText()) }
                }
                if (hasEndDateTime) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        endDate?.let { Text(it.toUserFriendlyText()) }
                        endTime?.let { Text(it.toUserFriendlyText()) }
                    }
                }
            }
        }
    }

    @Composable
    private fun OccurrenceColumnFromUntil() {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.from))
                Text(":")
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.until))
                Text(":")
            }
        }
    }
}
