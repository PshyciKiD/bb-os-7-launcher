package com.bblauncher.data.iconpack

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

/**
 * Orchestrates icon resolution through a three-tier chain:
 *   1. Built-in icons (our bundled assets/appfilter.xml + res/drawable/)
 *   2. Selected external icon pack (third-party APK)
 *   3. System default (PackageManager fallback — passed as parameter)
 *
 * Call [setActivePack] to configure the external pack before resolving.
 */
class IconPackResolver(private val context: Context) {

    /** Built-in icon mappings from our bundled appfilter.xml. */
    private val builtInMappings: Map<ComponentName, String> by lazy { loadBuiltInMappings() }

    /** Currently active external icon pack provider, or null for "System default". */
    private var externalProvider: IconPackProvider? = null

    /**
     * Sets the active external icon pack by [packageName].
     * Pass null to clear and fall back to system icons only.
     */
    fun setActivePack(packageName: String?) {
        externalProvider = if (packageName != null) {
            try {
                IconPackProvider(context, packageName)
            } catch (_: Exception) {
                // Pack may have been uninstalled between discovery and selection
                null
            }
        } else {
            null
        }
    }

    /**
     * Resolves an icon for the given [componentName].
     * Walks the chain: built-in → external pack → [systemIcon] fallback.
     */
    fun resolve(componentName: ComponentName, systemIcon: Drawable): Drawable {
        // 1. Try built-in icon mappings (our bundled overrides)
        resolveBuiltIn(componentName)?.let { return it }

        // 2. Try the active external icon pack
        externalProvider?.getIcon(componentName)?.let { return it }

        // 3. Fall back to system default
        return systemIcon
    }

    /** Attempts to load a built-in icon from our own res/drawable/. */
    private fun resolveBuiltIn(componentName: ComponentName): Drawable? {
        val drawableName = builtInMappings[componentName] ?: return null
        val resId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
        if (resId == 0) return null
        return try {
            ResourcesCompat.getDrawable(context.resources, resId, context.theme)
        } catch (_: Exception) {
            null
        }
    }

    /** Parses our bundled assets/appfilter.xml for built-in icon mappings. */
    private fun loadBuiltInMappings(): Map<ComponentName, String> {
        return try {
            context.assets.open("appfilter.xml").use { AppFilterParser.parse(it) }
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
