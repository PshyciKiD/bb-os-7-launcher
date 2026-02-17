package com.bblauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bblauncher.data.AppInfo
import com.bblauncher.data.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Category tabs shown above the icon tray. */
enum class Tab { Frequent, All, Favorites }

/**
 * Central state holder for the launcher.
 * Loads apps from [AppRepository] and exposes reactive state for the UI.
 */
class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    /** All launchable apps on the device. */
    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())

    /** Currently selected category tab. */
    private val _selectedTab = MutableStateFlow(Tab.All)
    val selectedTab: StateFlow<Tab> = _selectedTab.asStateFlow()

    /** Whether the icon tray is expanded to full screen. */
    private val _trayExpanded = MutableStateFlow(false)
    val trayExpanded: StateFlow<Boolean> = _trayExpanded.asStateFlow()

    /** Physical keyboard search query — filters apps by label. */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Apps filtered by the active tab + search query.
     * In MVP, only "All" tab is functional — Frequent/Favorites return the full list.
     */
    val filteredApps: StateFlow<List<AppInfo>> = combine(
        _allApps,
        _selectedTab,
        _searchQuery,
    ) { apps, _, query ->
        if (query.isBlank()) {
            apps
        } else {
            apps.filter { it.label.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            _allApps.value = repository.loadApps()
        }
    }

    fun selectTab(tab: Tab) {
        _selectedTab.value = tab
    }

    fun setTrayExpanded(expanded: Boolean) {
        _trayExpanded.value = expanded
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Collapses the tray or clears the search.
     * Returns true if something was collapsed/cleared (i.e. back was consumed).
     */
    fun handleBack(): Boolean {
        return when {
            _searchQuery.value.isNotBlank() -> {
                _searchQuery.value = ""
                true
            }
            _trayExpanded.value -> {
                _trayExpanded.value = false
                true
            }
            else -> false
        }
    }
}
