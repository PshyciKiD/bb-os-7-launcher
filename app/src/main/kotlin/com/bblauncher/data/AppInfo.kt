package com.bblauncher.data

import android.content.ComponentName
import android.graphics.drawable.Drawable

/**
 * Represents a launchable app discovered via PackageManager.
 * [icon] is the app's launcher icon drawable, rendered via [com.bblauncher.util.DrawablePainter].
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable,
) {
    /** ComponentName used to build a launch intent. */
    val componentName: ComponentName
        get() = ComponentName(packageName, activityName)
}
