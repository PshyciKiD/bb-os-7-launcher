package com.bblauncher.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bblauncher.data.AppInfo
import com.bblauncher.ui.theme.BBTheme
import com.bblauncher.util.rememberDrawablePainter

/**
 * App icon composable — renders the icon at full dock-row height with a label below.
 * No artificial overlays; BB7 theme icons already have gloss and shape baked in.
 * Supports both tap and trackpad center-click (DPAD_CENTER) to launch.
 */
@Composable
fun AppIcon(
    appInfo: AppInfo,
    onClick: () -> Unit,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .focusable()
            .clickable(onClick = onClick)
            // Trackpad center-click launches the app
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp && event.key == Key.DirectionCenter) {
                    onClick()
                    true
                } else {
                    false
                }
            },
    ) {
        // Icon — no border, shadow, or clip; rendered at native aspect ratio
        Image(
            painter = rememberDrawablePainter(appInfo.icon),
            contentDescription = appInfo.label,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(BBTheme.iconSize),
        )

        // Label below the icon (hidden in collapsed dock mode)
        if (showLabel) {
            Text(
                text = appInfo.label,
                style = TextStyle(
                    color = BBTheme.labelColor,
                    fontSize = BBTheme.labelSize,
                    shadow = Shadow(
                        color = BBTheme.labelShadowColor,
                        offset = Offset(1f, 1f),
                        blurRadius = BBTheme.labelShadowRadius,
                    ),
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
