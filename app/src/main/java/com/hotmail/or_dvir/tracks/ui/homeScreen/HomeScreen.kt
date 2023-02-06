package com.hotmail.or_dvir.tracks.ui.homeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.example.tracks.R
import com.hotmail.or_dvir.tracks.collectAsStateLifecycleAware
import com.hotmail.or_dvir.tracks.models.TrackedEvent

// todo
//  create new trackable event
//  delete trackable event
//      also delete all instances of this event!!!

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getViewModel<HomeScreenViewModel>()

        Scaffold(
            //todo
            // do i want a top app bar here?
            // should the top app bar be shared for all screens?
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.screenTitle_homeScreen)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(
                        contentDescription = stringResource(R.string.contentDescription_addTrackedEvent),
                        imageVector = Icons.Filled.Add
                    )
                }
            }
        ) {
            val trackedEvents =
                viewModel.trackedEventsFlow.collectAsStateLifecycleAware(initial = emptyList()).value

            LazyColumn(
                //todo what does this do? do i need this???
                contentPadding = it
            ) {
                itemsIndexed(trackedEvents) { index, item ->
                    TrackedEventRow(
                        event = item,
                        onRowClick = { /*todo*/ },
                        onQuickAdd = { /*todo*/ },
                    )

                    if (index != trackedEvents.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }

    @Composable
    private fun TrackedEventRow(
        event: TrackedEvent,
        onQuickAdd: () -> Unit,
        onRowClick: () -> Unit
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRowClick() }
                .padding(16.dp)
        ) {
            Text(
                text = event.name
            )

            IconButton(onClick = onQuickAdd) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = stringResource(R.string.contentDescription_quickAdd)
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun TrackedEventRowPreview() {
        TrackedEventRow(
            TrackedEvent(
                name = "event name"
            ),
            onQuickAdd = {},
            onRowClick = {}
        )
    }
}
