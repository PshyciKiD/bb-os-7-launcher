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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
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
 * Row 1: battery + date (left) | large time (center) | signal indicators (right)
 * Row 2: volume (left) | notification count + mail (center) | search (right)
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
            .padding(top = 6.dp, start = 8.dp, end = 8.dp),
    ) {
        // Row 1: battery + date pinned top-left, clock bottom-center, signals top-right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            // Left: battery + date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopStart),
            ) {
                BatteryIndicator(batteryLevel)
                Spacer(Modifier.width(6.dp))
                DateDisplay(currentTime)
            }

            // Center: large clock
            TimeDisplay(currentTime, modifier = Modifier.align(Alignment.BottomCenter))

            // Right: signal indicators
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Icon(
                    imageVector = Icons.Filled.SignalCellular4Bar,
                    contentDescription = "Signal",
                    tint = BBTheme.dimWhite,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = "WiFi",
                    tint = BBTheme.dimWhite,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Bluetooth,
                    contentDescription = "Bluetooth",
                    tint = BBTheme.dimWhite,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        // Row 2: volume (left) | notification + mail (center) | search (right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: volume icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Volume",
                tint = BBTheme.dimWhite,
                modifier = Modifier.size(16.dp),
            )

            Spacer(Modifier.weight(1f))

            // Center: notification count + mail icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "3",
                    style = TextStyle(
                        color = BBTheme.dimWhite,
                        fontSize = 12.sp,
                        shadow = Shadow(
                            color = BBTheme.labelShadowColor,
                            offset = Offset(1f, 1f),
                            blurRadius = 2f,
                        ),
                    ),
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Notifications",
                    tint = BBTheme.dimWhite,
                    modifier = Modifier.size(14.dp),
                )
            }

            Spacer(Modifier.weight(1f))

            // Right: search icon
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = BBTheme.dimWhite,
                modifier = Modifier.size(16.dp),
            )
        }

        // Metallic divider at the bottom of the status bar
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .height(1.dp)
                .background(BBTheme.statusBarDividerColor),
        )
    }
}
