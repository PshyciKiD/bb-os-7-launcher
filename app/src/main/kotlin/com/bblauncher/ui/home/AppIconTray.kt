package com.bblauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
 * 6-column icon grid at the bottom of the screen.
 *
 * When collapsed, shows the pinned [dockApps] row (BB7 default: Mail, SMS,
 * Contacts, Browser, Media, Calendar). When expanded via swipe-up, shows the
 * full [apps] list filling up to [maxHeight].
 */
@Composable
fun AppIconTray(
    dockApps: List<AppInfo>,
    apps: List<AppInfo>,
    isExpanded: Boolean,
    maxHeight: Dp,
    onExpandChange: (Boolean) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Swipe threshold in pixels
    val swipeThreshold = 50f

    // Choose which list to display: dock row when collapsed, all apps when expanded
    val displayApps = if (isExpanded) apps else dockApps

    // Use explicit height: collapsed = single dock row, expanded = fill available space
    val trayHeight = if (isExpanded) maxHeight else BBTheme.trayCollapsedHeight

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BBTheme.trayBackground)
            .height(trayHeight)
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
            items(displayApps, key = { it.packageName }) { app ->
                AppIcon(
                    appInfo = app,
                    onClick = { onAppClick(app) },
                    showLabel = isExpanded,
                )
            }
        }
    }
}
