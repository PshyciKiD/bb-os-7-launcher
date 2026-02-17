package com.bblauncher.ui.statusbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bblauncher.ui.theme.BBTheme

/** Reusable text style for status bar items — white with a subtle black shadow. */
private val statusTextStyle = TextStyle(
    color = BBTheme.textWhite,
    fontSize = 13.sp,
    shadow = Shadow(
        color = BBTheme.labelShadowColor,
        offset = Offset(1f, 1f),
        blurRadius = 2f,
    ),
)

/** Large time display for the status bar (e.g. "12:21 PM"). */
@Composable
fun TimeDisplay(millis: Long, modifier: Modifier = Modifier) {
    Text(
        text = formatTime(millis),
        style = statusTextStyle.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        ),
        modifier = modifier,
    )
}

/** Date display (e.g. "Fri, May 20"). */
@Composable
fun DateDisplay(millis: Long, modifier: Modifier = Modifier) {
    Text(
        text = formatDate(millis),
        style = statusTextStyle,
        modifier = modifier,
    )
}

/** Battery percentage indicator (e.g. "85%"). */
@Composable
fun BatteryIndicator(level: Int, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        // Simple text-based battery for MVP
        Text(
            text = "\u26A1",  // ⚡
            fontSize = 12.sp,
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = "$level%",
            style = statusTextStyle.copy(fontSize = 11.sp),
        )
    }
}
