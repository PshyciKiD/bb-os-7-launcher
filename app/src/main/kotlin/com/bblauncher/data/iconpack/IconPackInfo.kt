package com.bblauncher.data.iconpack

/**
 * Describes an installed icon pack discovered on the device.
 * [packageName] is the pack's APK package, [label] is the human-readable name.
 */
data class IconPackInfo(
    val packageName: String,
    val label: String,
)
