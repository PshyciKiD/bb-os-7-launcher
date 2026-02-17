package com.bblauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bblauncher.data.AppInfo
import com.bblauncher.ui.statusbar.BBStatusBar
import com.bblauncher.ui.theme.BBTheme
import com.bblauncher.viewmodel.Tab

/**
 * Full home screen layout:
 *   - Status bar (top)
 *   - Wallpaper area (flexible middle — transparent, shows system wallpaper)
 *   - Category tab bar
 *   - Icon tray (bottom, expandable)
 */
@Composable
fun HomeScreen(
    currentTime: Long,
    batteryLevel: Int,
    selectedTab: Tab,
    apps: List<AppInfo>,
    trayExpanded: Boolean,
    searchQuery: String,
    onTabSelected: (Tab) -> Unit,
    onTrayExpandChange: (Boolean) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Max tray height = screen height minus status bar
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val maxTrayHeight = screenHeightDp - BBTheme.statusBarHeight - BBTheme.tabBarHeight

    Column(modifier = modifier.fillMaxSize()) {
        // Status bar
        BBStatusBar(
            currentTime = currentTime,
            batteryLevel = batteryLevel,
        )

        // Wallpaper area — transparent spacer that shrinks when tray expands
        if (!trayExpanded) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        // Search query indicator — shows when the user is typing
        if (searchQuery.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BBTheme.tabBarBackground)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "\uD83D\uDD0D",  // 🔍
                    fontSize = 14.sp,
                )
                Text(
                    text = searchQuery,
                    color = BBTheme.textWhite,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        // Category tab bar
        CategoryTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
        )

        // Icon tray
        AppIconTray(
            apps = apps,
            isExpanded = trayExpanded,
            maxHeight = maxTrayHeight,
            onExpandChange = onTrayExpandChange,
            onAppClick = onAppClick,
        )
    }
}
