package com.hotmail.or_dvir.tracks.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tracks.R

@Composable
fun DeleteConfirmationDialog(
    @StringRes messageRes: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = { Text(stringResource(messageRes)) }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.SwipeToDelete(
    onDeleteRequested: () -> Unit,
    dismissContent: @Composable RowScope.() -> Unit
) {
    val deleteDirection by remember { mutableStateOf(DismissDirection.StartToEnd) }
    val deleteDismissValue by remember { mutableStateOf(DismissValue.DismissedToEnd) }
    //todo add option for endToStart swiping

    val dismissState = rememberDismissState(
        confirmStateChange = {
            when (it) {
                deleteDismissValue -> onDeleteRequested()
                DismissedToStart -> {
                    //todo when adding option for second swipe direction
                }
                else -> { /* do nothing. */
                }
            }

            //its up to the caller to actually "dismiss" the item
            // e.g. remove it from the data source
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
        directions = setOf(deleteDirection),
        dismissContent = dismissContent,
        background = {
            //should help with performance
            dismissState.dismissDirection?.apply {
                if (this == deleteDirection) {
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
            }
        }
    )
}

@Composable
fun TracksDialog(
    @StringRes titleRes: Int,
    @StringRes positiveButtonRes: Int,
    onPositiveButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    @StringRes negativeButtonRes: Int = R.string.cancel,
    content: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                //title
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.height(16.dp))

                //body
                content()

                Spacer(modifier = Modifier.height(5.dp))

                //buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    //negative button
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(negativeButtonRes))
                    }

                    //negative button
                    TextButton(
                        onClick = onPositiveButtonClick
                    ) {
                        Text(stringResource(positiveButtonRes))
                    }
                }
            }
        }
    }
}
