package com.bblauncher.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bblauncher.ui.home.HomeScreen
import com.bblauncher.ui.statusbar.rememberBatteryLevel
import com.bblauncher.ui.statusbar.rememberCurrentTime
import com.bblauncher.viewmodel.LauncherViewModel

/**
 * Top-level orchestrator composable.
 * Connects [LauncherViewModel] state to the [HomeScreen] UI.
 */
@Composable
fun BBLauncherRoot(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Live providers
    val currentTime by rememberCurrentTime()
    val batteryLevel by rememberBatteryLevel()

    // ViewModel state
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val trayExpanded by viewModel.trayExpanded.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    HomeScreen(
        currentTime = currentTime,
        batteryLevel = batteryLevel,
        selectedTab = selectedTab,
        apps = filteredApps,
        trayExpanded = trayExpanded,
        searchQuery = searchQuery,
        onTabSelected = viewModel::selectTab,
        onTrayExpandChange = viewModel::setTrayExpanded,
        onAppClick = { appInfo ->
            // Launch the app via its component name
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = appInfo.componentName
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivity(intent)
        },
        modifier = modifier,
    )
}
