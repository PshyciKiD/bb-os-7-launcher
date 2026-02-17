package com.bblauncher.ui.statusbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery0Bar
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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

/** Large time display with smaller AM/PM suffix (e.g. "12:21" large + "PM" small). */
@Composable
fun TimeDisplay(millis: Long, modifier: Modifier = Modifier) {
    val timeParts = formatTime(millis).split(" ")  // ["12:21", "PM"]
    val digits = timeParts.getOrElse(0) { "" }
    val amPm = timeParts.getOrElse(1) { "" }

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 26.sp, fontWeight = FontWeight.Light)) {
                append(digits)
            }
            append(" ")
            withStyle(SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)) {
                append(amPm)
            }
        },
        style = statusTextStyle.copy(letterSpacing = 1.sp),
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

/** Battery icon that changes based on charge level, plus percentage text. */
@Composable
fun BatteryIndicator(level: Int, modifier: Modifier = Modifier) {
    val batteryIcon = when {
        level >= 95 -> Icons.Filled.BatteryFull
        level >= 60 -> Icons.Filled.Battery6Bar
        level >= 40 -> Icons.Filled.Battery4Bar
        level >= 15 -> Icons.Filled.Battery2Bar
        else -> Icons.Filled.Battery0Bar
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            imageVector = batteryIcon,
            contentDescription = "Battery $level%",
            tint = BBTheme.textWhite,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = "$level%",
            style = statusTextStyle.copy(fontSize = 11.sp),
        )
    }
}
