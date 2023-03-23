package com.hotmail.or_dvir.tracks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.flow.Flow

private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatterBuilder()
    .append(dateFormatter)
    .appendLiteral(" ")
    .append(timeFormatter)
    .toFormatter()

private fun Long.millisToZonedDateTime() =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())

fun LocalDateTime.toEpochMillis() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun LocalDateTime.toUserFriendlyText() = this.format(dateTimeFormatter)
fun LocalDate.toUserFriendlyText() = this.format(dateFormatter)
fun LocalTime.toUserFriendlyText() = this.format(timeFormatter)

fun Long.millisToLocalDateTime() = this.millisToZonedDateTime().toLocalDateTime()
fun Long.millisToLocalDate() = this.millisToZonedDateTime().toLocalDate()
fun Long.millisToLocalTime() = this.millisToZonedDateTime().toLocalTime()

@Composable
fun <T> rememberFlow(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): Flow<T> {
    return remember(
        key1 = flow,
        key2 = lifecycleOwner
    ) { flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED) }
}

@Composable
fun <T : R, R> Flow<T>.collectAsStateLifecycleAware(
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> {
    val lifecycleAwareFlow = rememberFlow(flow = this)
    return lifecycleAwareFlow.collectAsState(initial = initial, context = context)
}