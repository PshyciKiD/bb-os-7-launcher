package com.bblauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bblauncher.data.AppInfo
import com.bblauncher.data.iconpack.IconPackInfo
import com.bblauncher.ui.statusbar.BBStatusBar
import com.bblauncher.ui.theme.BBTheme
import com.bblauncher.viewmodel.Tab

/**
 * Full home screen layout:
 *   - Status bar (top)
 *   - Wallpaper area (flexible middle — transparent, shows system wallpaper)
 *   - Category tab bar
 *   - Icon tray (bottom, expandable)
 *
 * Also hosts the icon pack picker dialog, triggered by long-press on the tab bar.
 */
@Composable
fun HomeScreen(
    currentTime: Long,
    batteryLevel: Int,
    selectedTab: Tab,
    dockApps: List<AppInfo>,
    apps: List<AppInfo>,
    trayExpanded: Boolean,
    searchQuery: String,
    showIconPackPicker: Boolean,
    availablePacks: List<IconPackInfo>,
    activeIconPack: String?,
    onTabSelected: (Tab) -> Unit,
    onTrayExpandChange: (Boolean) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onIconPackPickerRequested: () -> Unit,
    onIconPackPickerDismissed: () -> Unit,
    onIconPackSelected: (String?) -> Unit,
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

        // Category tab bar — tap active tab to toggle drawer, long-press for icon pack picker
        CategoryTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onToggleDrawer = { onTrayExpandChange(!trayExpanded) },
            onLongPress = onIconPackPickerRequested,
        )

        // Icon tray — dock row shown when collapsed, full grid when expanded
        AppIconTray(
            dockApps = dockApps,
            apps = apps,
            isExpanded = trayExpanded,
            maxHeight = maxTrayHeight,
            onExpandChange = onTrayExpandChange,
            onAppClick = onAppClick,
        )
    }

    // Icon pack picker dialog
    if (showIconPackPicker) {
        IconPackPickerDialog(
            packs = availablePacks,
            activePackage = activeIconPack,
            onSelect = { selected ->
                onIconPackSelected(selected)
                onIconPackPickerDismissed()
            },
            onDismiss = onIconPackPickerDismissed,
        )
    }
}

/**
 * Simple dialog with radio buttons for choosing an icon pack.
 * "System default" is always the first option (represented by null).
 */
@Composable
private fun IconPackPickerDialog(
    packs: List<IconPackInfo>,
    activePackage: String?,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    // Track local selection so user can preview before confirming
    var selected by remember { mutableStateOf(activePackage) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Icon Pack", color = Color.White) },
        containerColor = Color(0xFF1A1A1A),
        text = {
            Column {
                // "System default" option
                IconPackRadioRow(
                    label = "System default",
                    isSelected = selected == null,
                    onClick = { selected = null },
                )

                // Installed third-party packs
                packs.forEach { pack ->
                    IconPackRadioRow(
                        label = pack.label,
                        isSelected = selected == pack.packageName,
                        onClick = { selected = pack.packageName },
                    )
                }

                if (packs.isEmpty()) {
                    Text(
                        text = "No icon packs installed.\nInstall one from the Play Store.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSelect(selected) }) {
                Text("Apply", color = BBTheme.textWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
    )
}

/** A single radio button row in the icon pack picker. */
@Composable
private fun IconPackRadioRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = BBTheme.textWhite,
                unselectedColor = Color.Gray,
            ),
        )
        Text(
            text = label,
            color = BBTheme.textWhite,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}
