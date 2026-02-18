package com.bblauncher.data.iconpack

import android.content.ComponentName
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

/**
 * Loads themed icon drawables from a specific icon pack's APK resources.
 * Lazily parses the pack's appfilter.xml on first icon request, trying
 * assets/ first, then falling back to res/xml/.
 */
class IconPackProvider(
    private val context: Context,
    val packageName: String,
) {
    /** The icon pack's resources, used to load drawables. */
    private val packResources: Resources = context.packageManager
        .getResourcesForApplication(packageName)

    /** Cached appfilter mappings: ComponentName → drawable resource name. */
    private val mappings: Map<ComponentName, String> by lazy { loadMappings() }

    /**
     * Returns the themed drawable for [componentName], or null if this pack
     * doesn't provide an icon for it.
     */
    fun getIcon(componentName: ComponentName): Drawable? {
        val drawableName = mappings[componentName] ?: return null
        return loadDrawable(drawableName)
    }

    /** Loads a drawable by resource name from the pack's APK. */
    private fun loadDrawable(name: String): Drawable? {
        val resId = packResources.getIdentifier(name, "drawable", packageName)
        if (resId == 0) return null
        return try {
            ResourcesCompat.getDrawable(packResources, resId, null)
        } catch (_: Resources.NotFoundException) {
            null
        }
    }

    /**
     * Parses the pack's appfilter.xml, trying assets/ first (most common),
     * then falling back to res/xml/ (some packs use this instead).
     */
    private fun loadMappings(): Map<ComponentName, String> {
        tryParseFromAssets()?.let { return it }
        tryParseFromResXml()?.let { return it }
        return emptyMap()
    }

    /** Tries to parse appfilter.xml from the pack's assets/ directory. */
    private fun tryParseFromAssets(): Map<ComponentName, String>? {
        return try {
            packResources.assets
                .open("appfilter.xml")
                .use { AppFilterParser.parse(it) }
        } catch (_: Exception) {
            null
        }
    }

    /** Tries to parse appfilter from the pack's compiled res/xml/ resource. */
    private fun tryParseFromResXml(): Map<ComponentName, String>? {
        return try {
            val resId = packResources.getIdentifier("appfilter", "xml", packageName)
            if (resId == 0) return null
            val xmlParser = packResources.getXml(resId)
            val result = AppFilterParser.parse(xmlParser)
            xmlParser.close()
            result
        } catch (_: Exception) {
            null
        }
    }
}
