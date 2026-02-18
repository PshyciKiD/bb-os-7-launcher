package com.bblauncher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.bblauncher.data.iconpack.IconPackResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Queries PackageManager for all launchable apps.
 * Uses [IconPackResolver] to apply themed icons from the resolution chain
 * (built-in → external pack → system default).
 * Runs on [Dispatchers.IO] since PM queries and icon loading can be slow.
 */
class AppRepository(
    private val context: Context,
    private val iconResolver: IconPackResolver,
) {

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

    /** Convert a [ResolveInfo] into our [AppInfo] model, resolving themed icons. */
    private fun ResolveInfo.toAppInfo(pm: PackageManager): AppInfo? {
        val ai = activityInfo ?: return null
        val component = ComponentName(ai.packageName, ai.name)
        val systemIcon = ai.loadIcon(pm)
        return AppInfo(
            label = ai.loadLabel(pm).toString(),
            packageName = ai.packageName,
            activityName = ai.name,
            icon = iconResolver.resolve(component, systemIcon),
        )
    }
}
