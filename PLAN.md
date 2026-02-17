# BB Launcher MVP - BlackBerry OS 7 Home Screen for Zinwa Q25

## Context

The Zinwa Q25 is a BlackBerry Classic body with Android 14 internals (MediaTek Helio G99, 12GB RAM, 720x720 square IPS display, physical QWERTY keyboard, trackpad, LED). No existing launcher recreates the BB OS 7 skeuomorphic home screen experience. We're building one from scratch — single-device target, no need for multi-screen/platform support.

The repo is currently empty (just a README + git init).

## Reference: BB OS 7 Home Screen Layout (from screenshot)

```
┌──────────────────────────────────┐
│ Fri, May 20    12:21 PM   3G ⊙ ≋│  ← Row 1: date / large time / network+BT
│ 🔊           ✉ 3          🔍    │  ← Row 2: volume / notif count / search
├──────────────────────────────────┤
│                                  │
│                                  │
│         (wallpaper area)         │  ← Wallpaper-dominant, no icons here
│                                  │
│                                  │
├──────────────────────────────────┤
│  Frequent  │  All  │  Favorites │  ← Category tabs (horizontal)
├──────────────────────────────────┤
│ ✉  💬  👤  🌐  📁  📅          │  ← 6-column icon row(s), scrollable
│ Msg Chat Cont Brws File Cal     │     with labels below each icon
└──────────────────────────────────┘
```

Key observations:
- **Wallpaper-dominant** — the middle of the screen is open wallpaper, not an icon grid
- **Two-row status bar** with date, time, indicators, notifications, search
- **Category tabs** ("Frequent" | "All" | "Favorites") filter which apps show in the tray
- **6-column icon tray** at the bottom — this IS the app browser, no separate drawer
- **Swipe up** expands the icon tray to show more rows (full-screen app browsing)
- **No dedicated app drawer icon** — the tab tray + swipe gesture replaces it

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (best fit for custom skeuomorphic graphics)
- **Build:** Gradle (Kotlin DSL), single `app` module
- **Architecture:** Single Activity, ViewModel + StateFlow, no DI framework for MVP
- **Target:** `minSdk = 34` (Android 14 only), `compileSdk`/`targetSdk` = latest stable
- **Dev environment:** Nix flake + direnv (`.envrc` with `use flake`)

> **Note on dependency versions:** Exact AGP, Kotlin, and Compose BOM versions will be pinned to latest stable at setup time.

## MVP Scope

### In Scope
| Feature | Detail |
|---|---|
| Two-row status bar | Row 1: date, time (large), network indicators. Row 2: notification count, search icon |
| Wallpaper-dominant home | System wallpaper fills the middle area via transparent Activity theme |
| Category tab bar | "All" tab shows all apps. "Frequent" + "Favorites" tabs shown but non-functional in MVP |
| 6-column icon tray | Bottom icon grid, scrollable, BB OS 7 skeuomorphic styling |
| Swipe-up expansion | Swipe up on icon tray → expands to full screen for browsing all apps |
| App launching | Tap icon → launch app |
| Default tray apps | First row: Messages, Contacts, Calendar, Browser, Files, Phone |
| Skeuomorphic icon styling | Drop shadows, specular highlight overlay, rounded corners, text shadows |
| Physical keyboard search | Type on QWERTY → filters apps in the tray |
| Trackpad navigation | D-pad focus navigation through icon grid, center-click to launch |
| Back button handling | Back collapses expanded tray; never finishes the launcher Activity |

### Out of Scope (post-MVP)
"Frequent" tab logic (usage tracking), "Favorites" tab (user pinning), notification badges, widgets, themes/settings, folders, icon packs, drag-and-drop reorder, LED control, notification listener service, search icon action

## Architecture

```
MainActivity (singleTask, HOME+DEFAULT intent filters)
  └── setContent { BBLauncherRoot(viewModel) }
        ├── BBStatusBar               ← two-row dark gradient bar
        │     ├── Row 1: date / time / indicators
        │     └── Row 2: notif count / search icon
        ├── Spacer(weight)            ← wallpaper shows through here
        ├── CategoryTabBar            ← "Frequent" | "All" | "Favorites"
        └── AppIconTray               ← 6-col scrollable grid, swipe-up expandable
              └── AppIcon (×N)        ← skeuomorphic icon + label
```

**State:** `LauncherViewModel` exposes:
- `allApps: StateFlow<List<AppInfo>>` — all launchable apps, sorted alphabetically
- `selectedTab: StateFlow<Tab>` — which category tab is active (only "All" functional in MVP)
- `trayExpanded: StateFlow<Boolean>` — whether the icon tray is in expanded (full-screen) mode
- `searchQuery: StateFlow<String>` — keyboard search filter
- `filteredApps: StateFlow<List<AppInfo>>` — apps matching tab + search query

**App discovery:** `PackageManager.queryIntentActivities(ACTION_MAIN + CATEGORY_LAUNCHER)` on `Dispatchers.IO`.

**Wallpaper:** Transparent Activity theme (`windowShowWallpaper=true`) — no bitmap allocation, supports live wallpapers, auto-updates.

## Visual Design

### Layout Math (720×720 px, ~360×360 dp at ~2x density)
- Status bar: **~48dp** (two rows × ~24dp each) — dark gradient
- Wallpaper area: **flexible** — fills remaining space (shrinks when tray expands)
- Tab bar: **~36dp** — category selector
- Icon tray (collapsed): **~180dp** — shows ~2 rows of 6 icons
- Icon tray (expanded): fills screen below status bar (~312dp)
- Physical nav buttons on device → no Android software nav bar

### Skeuomorphic Styling
- **Icons:** Drop shadow (2dp offset, 4dp blur, 40% black), specular highlight gradient (top-half white→transparent overlay), rounded corners (12dp), ~48dp icon size
- **Text labels:** White ~10sp, centered below icon, with black text shadow for wallpaper readability
- **Status bar:** Near-black vertical gradient (`#1A1A1A` → `#2D2D2D`), 1dp metallic divider at bottom
- **Tab bar:** Semi-transparent dark background, active tab has subtle highlight/underline, white text
- **Icon tray background:** Semi-transparent dark (`~80% opaque #111111`) so wallpaper subtly shows through

## File Structure (~20 source files)

```
bblauncher/
  flake.nix                             # Nix flake — Android SDK, JDK 17, build tools
  flake.lock
  .envrc                                # `use flake` for direnv
  settings.gradle.kts
  build.gradle.kts
  gradle.properties
  gradle/wrapper/gradle-wrapper.properties
  app/
    build.gradle.kts
    src/main/
      AndroidManifest.xml
      res/values/{strings.xml, themes.xml}
      kotlin/com/bblauncher/
        BBLauncherApp.kt                # Application subclass
        MainActivity.kt                 # Single Activity, hosts Compose
        data/
          AppInfo.kt                    # Data class (label, packageName, activityName, icon)
          AppRepository.kt             # PackageManager queries
        viewmodel/
          LauncherViewModel.kt          # Central state: apps, tabs, tray, search
        ui/
          BBLauncherRoot.kt             # Top-level orchestrator composable
          theme/BBTheme.kt              # Colors, dimensions, brushes
          statusbar/
            BBStatusBar.kt              # Two-row status bar composable
            StatusIndicators.kt         # Date, time, battery, signal helpers
            TimeProvider.kt             # Composable effect for live clock
            BatteryProvider.kt          # Composable effect for battery level
          home/
            HomeScreen.kt              # Wallpaper area + tray layout
            AppIcon.kt                  # Skeuomorphic icon + label
            CategoryTabBar.kt           # "Frequent" | "All" | "Favorites" tabs
            AppIconTray.kt              # 6-col scrollable grid, swipe-up expandable
        util/
          DrawablePainter.kt           # Drawable → Compose Painter helper
```

## Implementation Order

### Phase 0: Dev Environment
1. Create `flake.nix` with Android SDK (platforms 34-35, build-tools, platform-tools), JDK 17
2. Create `.envrc` with `use flake`
3. **Verify:** `direnv exec . java -version` and `direnv exec . adb --version` work

### Phase 1: Project Skeleton
4. Gradle wrapper, `settings.gradle.kts`, root `build.gradle.kts`, `gradle.properties`
5. `app/build.gradle.kts` with Compose dependencies
6. `AndroidManifest.xml` + `themes.xml` + `strings.xml` (launcher intent filters, transparent wallpaper theme)
7. `BBLauncherApp.kt` + `MainActivity.kt` with placeholder `setContent { Text("Hello BB") }`
8. **Verify:** `direnv exec . ./gradlew assembleDebug` builds successfully

### Phase 2: Data Layer
9. `AppInfo.kt` + `AppRepository.kt` — query installed apps
10. `LauncherViewModel.kt` — loads apps, exposes StateFlows
11. `DrawablePainter.kt` — Drawable→Painter utility
12. **Verify:** app list loads and logs correctly

### Phase 3: Core Home Screen
13. `BBTheme.kt` — all visual constants (colors, dimensions, brushes)
14. `AppIcon.kt` — skeuomorphic icon with shadows + highlights + label
15. `AppIconTray.kt` — 6-column LazyVerticalGrid in bottom tray
16. `HomeScreen.kt` — wallpaper spacer + tray layout
17. `BBLauncherRoot.kt` (partial) — home screen only
18. **Verify:** icon tray renders at bottom, tapping launches apps, wallpaper visible above

### Phase 4: Status Bar + Tabs
19. `TimeProvider.kt` + `BatteryProvider.kt` + `StatusIndicators.kt`
20. `BBStatusBar.kt` — two-row status bar
21. `CategoryTabBar.kt` — tab selector (only "All" functional)
22. Wire into `BBLauncherRoot.kt`
23. **Verify:** full layout — status bar / wallpaper / tabs / icon tray

### Phase 5: Swipe-Up Expansion + Search
24. Add swipe-up gesture to `AppIconTray` — expands tray to full screen
25. Add physical keyboard search — typing filters apps in tray
26. Back button handling — collapses expanded tray, never finishes Activity
27. **Verify:** swipe up expands, type filters, back collapses

### Phase 6: Physical Input Polish
28. Trackpad D-pad focus navigation on AppIcon
29. Type-to-search auto-triggers from collapsed tray too
30. Visual polish pass — verify all spacing/shadows on 720×720

## Verification

```bash
# Build (all commands via direnv)
direnv exec . ./gradlew assembleDebug

# Install
direnv exec . adb install -r app/build/outputs/apk/debug/app-debug.apk

# Set as default launcher
direnv exec . adb shell cmd package set-home-activity com.bblauncher/.MainActivity

# Logs
direnv exec . adb logcat -s BBLauncher:D
```

**Test checklist:**
- [ ] App appears in launcher chooser
- [ ] Two-row status bar renders (date, time, battery)
- [ ] Wallpaper visible in middle area
- [ ] Tab bar shows ("All" active by default)
- [ ] 6-column icon tray at bottom with app icons + labels
- [ ] Tap icon → app launches
- [ ] Home button → returns to launcher
- [ ] Back button → collapses tray / no-op (never finishes)
- [ ] Swipe up → tray expands to full screen
- [ ] Swipe down / back → tray collapses
- [ ] Physical keyboard typing → filters apps
- [ ] Trackpad D-pad → navigates between icons
- [ ] Trackpad center → launches focused app
- [ ] Skeuomorphic styling visible (shadows, highlights, gradients)
