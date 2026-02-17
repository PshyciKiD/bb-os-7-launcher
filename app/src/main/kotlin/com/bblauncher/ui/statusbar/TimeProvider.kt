package com.bblauncher.ui.statusbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Provides a live clock tick that updates every second.
 * The returned [State] holds the current epoch millis — composables that
 * read it will recompose on each tick.
 */
@Composable
fun rememberCurrentTime(): State<Long> {
    val time = remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            time.longValue = System.currentTimeMillis()
            delay(1_000L)
        }
    }

    return time
}

/** Format millis → "12:21 PM" */
fun formatTime(millis: Long): String {
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
}

/** Format millis → "Fri, May 20" */
fun formatDate(millis: Long): String {
    return SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(millis))
}
