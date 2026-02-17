package com.bblauncher.util

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.core.graphics.drawable.toBitmap

/**
 * Converts an Android [Drawable] to a Compose [Painter].
 * Rasterizes to bitmap at [sizePx] × [sizePx] for consistent rendering.
 */
@Composable
fun rememberDrawablePainter(drawable: Drawable, sizePx: Int = 128): Painter {
    return remember(drawable) {
        val bitmap = drawable.toBitmap(sizePx, sizePx)
        BitmapPainter(bitmap.asImageBitmap())
    }
}
