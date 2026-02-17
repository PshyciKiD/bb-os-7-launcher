package com.bblauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Queries PackageManager for all launchable apps.
 * Runs on [Dispatchers.IO] since PM queries can be slow on first call.
 */
class AppRepository(private val context: Context) {

    /** Returns all launchable apps sorted alphabetically, excluding ourselves. */
    suspend fun loadApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        pm.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
            .filter { it.activityInfo.packageName != context.packageName }
            .mapNotNull { it.toAppInfo(pm) }
            .sortedBy { it.label.lowercase() }
    }

    /** Convert a [ResolveInfo] into our [AppInfo] model. */
    private fun ResolveInfo.toAppInfo(pm: PackageManager): AppInfo? {
        val ai = activityInfo ?: return null
        return AppInfo(
            label = ai.loadLabel(pm).toString(),
            packageName = ai.packageName,
            activityName = ai.name,
            icon = ai.loadIcon(pm),
        )
    }
}
