package com.bblauncher.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bblauncher.data.AppInfo
import com.bblauncher.data.AppRepository
import com.bblauncher.data.iconpack.IconPackDiscovery
import com.bblauncher.data.iconpack.IconPackInfo
import com.bblauncher.data.iconpack.IconPackResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Category tabs shown above the icon tray. */
enum class Tab { Frequent, All, Favorites }

private const val PREFS_NAME = "bblauncher_prefs"
private const val KEY_ICON_PACK = "icon_pack"

/**
 * Central state holder for the launcher.
 * Loads apps from [AppRepository] and exposes reactive state for the UI.
 * Manages icon pack selection, discovery, and persistence.
 */
class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val iconResolver = IconPackResolver(application)
    private val repository = AppRepository(application, iconResolver)

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

    /** Installed icon packs discovered on the device. */
    private val _availablePacks = MutableStateFlow<List<IconPackInfo>>(emptyList())
    val availablePacks: StateFlow<List<IconPackInfo>> = _availablePacks.asStateFlow()

    /** Package name of the currently active icon pack, or null for system default. */
    private val _activeIconPack = MutableStateFlow<String?>(null)
    val activeIconPack: StateFlow<String?> = _activeIconPack.asStateFlow()

    /** Whether the icon pack picker dialog should be shown. */
    private val _showIconPackPicker = MutableStateFlow(false)
    val showIconPackPicker: StateFlow<Boolean> = _showIconPackPicker.asStateFlow()

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
        // Restore persisted icon pack selection and apply it before loading apps
        val savedPack = prefs.getString(KEY_ICON_PACK, null)
        _activeIconPack.value = savedPack
        iconResolver.setActivePack(savedPack)

        viewModelScope.launch {
            _allApps.value = repository.loadApps()
        }

        // Discover available icon packs in the background
        viewModelScope.launch {
            _availablePacks.value = IconPackDiscovery.discover(getApplication())
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

    /** Shows the icon pack picker dialog. */
    fun requestIconPackPicker() {
        _showIconPackPicker.value = true
    }

    /** Dismisses the icon pack picker dialog. */
    fun dismissIconPackPicker() {
        _showIconPackPicker.value = false
    }

    /**
     * Sets the active icon pack and reloads all app icons.
     * Pass null to revert to system default icons.
     * Persists the selection to SharedPreferences.
     */
    fun setIconPack(packageName: String?) {
        _activeIconPack.value = packageName
        iconResolver.setActivePack(packageName)

        // Persist selection
        prefs.edit().apply {
            if (packageName != null) putString(KEY_ICON_PACK, packageName)
            else remove(KEY_ICON_PACK)
        }.apply()

        // Reload apps with new icons
        viewModelScope.launch {
            _allApps.value = repository.loadApps()
        }
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
