package com.bblauncher.data.iconpack

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * Discovers installed icon packs by querying PackageManager for the standard
 * icon pack intent filters used by ADW, Nova, GO Launcher, and Apex.
 */
object IconPackDiscovery {

    /** Standard intent actions that third-party icon packs advertise. */
    private val ICON_PACK_ACTIONS = listOf(
        "org.adw.launcher.THEMES",
        "com.novalauncher.THEME",
        "com.gau.go.launcherex.theme",
        "com.anddoes.launcher.THEME",
    )

    /** Returns all unique icon packs installed on the device, sorted by label. */
    fun discover(context: Context): List<IconPackInfo> {
        val pm = context.packageManager
        val seen = mutableSetOf<String>()
        val packs = mutableListOf<IconPackInfo>()

        for (action in ICON_PACK_ACTIONS) {
            val intent = Intent(action)
            val results = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            for (ri in results) {
                val pkg = ri.activityInfo.packageName
                // Deduplicate — packs often register multiple intent filters
                if (seen.add(pkg)) {
                    packs.add(
                        IconPackInfo(
                            packageName = pkg,
                            label = ri.loadLabel(pm).toString(),
                        ),
                    )
                }
            }
        }

        return packs.sortedBy { it.label.lowercase() }
    }
}
