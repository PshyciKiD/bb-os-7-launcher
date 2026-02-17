package com.bblauncher.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
 * Skeuomorphic app icon with drop shadow, specular highlight, and text label.
 * Supports both tap and trackpad center-click (DPAD_CENTER) to launch.
 */
@Composable
fun AppIcon(
    appInfo: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 6.dp)
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
        // Icon with drop shadow + specular highlight
        Box {
            val shape = RoundedCornerShape(BBTheme.iconCornerRadius)

            Image(
                painter = rememberDrawablePainter(appInfo.icon),
                contentDescription = appInfo.label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(BBTheme.iconSize)
                    .shadow(
                        elevation = BBTheme.iconShadowBlur,
                        shape = shape,
                    )
                    .clip(shape),
            )

            // Specular highlight overlay — top-half white→transparent
            Box(
                modifier = Modifier
                    .size(BBTheme.iconSize)
                    .clip(shape)
                    .background(BBTheme.iconHighlightBrush),
            )
        }

        // Label below the icon
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
