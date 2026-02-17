package com.bblauncher.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual constants for the BB OS 7 skeuomorphic look.
 * All dimensions tuned for the 720×720 (~360dp) square display.
 */
object BBTheme {

    // -- Status bar --
    val statusBarGradient = Brush.verticalGradient(
        colors = listOf(Color(0x99000000), Color(0x66000000)),  // semi-transparent black
    )
    val statusBarHeight = 56.dp       // taller row 1 + compact row 2
    val statusBarDividerColor = Color(0x88555555)  // semi-transparent metallic line

    // -- Tab bar --
    val tabBarBackground = Color(0xCC222222.toInt()) // ~80% opaque dark
    val tabBarHeight = 36.dp
    val tabActiveHighlight = Color(0xFF444444)
    val tabTextColor = Color.White
    val tabTextSize = 12.sp

    // -- Icon tray --
    val trayBackground = Color(0xCC111111.toInt())   // ~80% opaque near-black
    val trayCollapsedHeight = 84.dp   // single row of icons when collapsed (matches BB OS 7)
    val trayColumns = 6

    // -- App icons --
    val iconSize = 48.dp
    val iconCornerRadius = 12.dp
    val iconShadowOffset = 2.dp
    val iconShadowBlur = 4.dp
    val iconShadowColor = Color(0x66000000)          // 40% black

    // Specular highlight: top-half white→transparent overlay
    val iconHighlightBrush = Brush.verticalGradient(
        colors = listOf(Color(0x33FFFFFF), Color.Transparent),
    )

    // -- Labels --
    val labelColor = Color.White
    val labelSize = 10.sp
    val labelShadowColor = Color.Black
    val labelShadowRadius = 2f

    // -- General --
    val textWhite = Color.White
    val dimWhite = Color(0xAAFFFFFF.toInt())
}
