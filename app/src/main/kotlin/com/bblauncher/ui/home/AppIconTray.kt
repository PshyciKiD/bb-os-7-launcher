package com.bblauncher.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bblauncher.data.AppInfo
import com.bblauncher.ui.theme.BBTheme

/**
 * 6-column scrollable icon grid at the bottom of the screen.
 *
 * Supports swipe-up to expand to [maxHeight] and swipe-down to collapse
 * back to [BBTheme.trayCollapsedHeight].
 */
@Composable
fun AppIconTray(
    apps: List<AppInfo>,
    isExpanded: Boolean,
    maxHeight: Dp,
    onExpandChange: (Boolean) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Swipe threshold in pixels
    val swipeThreshold = 50f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BBTheme.trayBackground)
            .animateContentSize()
            .heightIn(
                min = BBTheme.trayCollapsedHeight,
                max = if (isExpanded) maxHeight else BBTheme.trayCollapsedHeight,
            )
            .pointerInput(isExpanded) {
                detectVerticalDragGestures { _, dragAmount ->
                    // Negative drag = swipe up → expand
                    if (dragAmount < -swipeThreshold && !isExpanded) {
                        onExpandChange(true)
                    }
                    // Positive drag = swipe down → collapse
                    if (dragAmount > swipeThreshold && isExpanded) {
                        onExpandChange(false)
                    }
                }
            },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(BBTheme.trayColumns),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        ) {
            items(apps, key = { it.packageName }) { app ->
                AppIcon(
                    appInfo = app,
                    onClick = { onAppClick(app) },
                )
            }
        }
    }
}
