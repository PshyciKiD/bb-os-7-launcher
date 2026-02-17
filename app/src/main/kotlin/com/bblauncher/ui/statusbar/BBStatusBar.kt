package com.bblauncher.ui.statusbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bblauncher.ui.theme.BBTheme

/**
 * Two-row BB OS 7 status bar.
 *
 * Row 1: date | large time | battery
 * Row 2: notification count placeholder | search icon placeholder
 */
@Composable
fun BBStatusBar(
    currentTime: Long,
    batteryLevel: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BBTheme.statusBarGradient)
            .padding(horizontal = 12.dp),
    ) {
        // Row 1: date / time / battery
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DateDisplay(currentTime)
            TimeDisplay(currentTime)
            BatteryIndicator(batteryLevel)
        }

        // Row 2: notification count / search icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Notification count placeholder
            Text(
                text = "\u2709",  // ✉
                fontSize = 14.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = BBTheme.labelShadowColor,
                        offset = Offset(1f, 1f),
                        blurRadius = 2f,
                    ),
                ),
            )
            Spacer(Modifier.weight(1f))
            // Search icon placeholder
            Text(
                text = "\uD83D\uDD0D",  // 🔍
                fontSize = 14.sp,
            )
        }

        // Metallic divider at the bottom of the status bar
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BBTheme.statusBarDividerColor),
        )
    }
}
