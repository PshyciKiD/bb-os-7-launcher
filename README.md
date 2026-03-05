# BB OS 7 Launcher

A BlackBerry OS 7-inspired Android launcher for the Zinwa Q25 (and other Android devices). Brings back the classic BB experience with authentic icons and layout.

> [!TIP]
> Contributions welcome! If you want to make this better, please do. All PRs welcome and accepted.

> [!WARNING]
> This project is far from complete or usable. It is vibe coded with Claude Code without much code review. 

![Android](https://img.shields.io/badge/Android-14+-green) ![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-purple)

![IMG_1118 Medium](https://github.com/user-attachments/assets/59ee344d-b959-4e3d-bc96-3ed6e7e6e05e)


## Features

- **BB7 Status Bar** -- Time, battery, and notification indicators styled after BlackBerry OS 7
- **Pinned Dock Row** -- Default dock mirrors the BB7 home screen: Mail, SMS, Contacts, Browser, Media, Calendar
- **App Drawer** -- Swipe up or tap the active tab to expand the full app grid; swipe down or tap again to collapse
- **Category Tabs** -- Frequent / All / Favorites tab bar (All is functional; others are placeholders)
- **Authentic BB7 Icons** -- 136 icons extracted from the BlackBerry 7.1 simulator theme (`net_rim_theme_BlackBerry7_480x640_t.cod`), with 25 mapped to common Android apps via `appfilter.xml`
- **Icon Pack Resolver** -- Three-tier icon resolution chain: built-in BB7 theme -> external icon pack -> system default. Long-press the tab bar to open the icon pack picker
- **Physical Keyboard Search** -- Start typing to filter apps by name (designed for the Q25's hardware keyboard)

## Building

Requires Android SDK 35+ and JDK 17+.

```bash
# Debug build
./gradlew assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

A Nix flake is provided for reproducible dev environments:

```bash
nix develop
```

## Project Structure

```
app/src/main/
  kotlin/com/bblauncher/
    data/
      AppInfo.kt              # App model (label, package, icon)
      AppRepository.kt        # Queries PackageManager, resolves themed icons
      iconpack/
        AppFilterParser.kt    # Parses appfilter.xml (standard icon pack format)
        IconPackDiscovery.kt  # Finds installed third-party icon packs
        IconPackProvider.kt   # Loads drawables from an external icon pack APK
        IconPackResolver.kt   # Three-tier resolution chain
        IconPackInfo.kt       # Data class for discovered packs
    ui/
      home/
        HomeScreen.kt         # Main layout: status bar, wallpaper, tabs, tray
        AppIconTray.kt        # Dock row (collapsed) / app grid (expanded)
        AppIcon.kt            # Single icon + label composable
        CategoryTabBar.kt     # Frequent | All | Favorites tabs
      statusbar/              # BB7-style status bar
      theme/BBTheme.kt        # Colors, sizes, layout constants
      BBLauncherRoot.kt       # Wires ViewModel to UI
    viewmodel/
      LauncherViewModel.kt    # Central state: apps, dock, tabs, icon packs
  assets/
    appfilter.xml             # Built-in icon mappings (ComponentName -> drawable)
  res/drawable/
    bb_*.png                  # 25 named BB7 icons (mapped to Android apps)
    bb7_*.png                 # 111 additional BB7 icons (unmapped, available for future use)
```
