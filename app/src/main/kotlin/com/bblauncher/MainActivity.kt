package com.bblauncher

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bblauncher.ui.BBLauncherRoot
import com.bblauncher.viewmodel.LauncherViewModel

/**
 * Single Activity launcher host.
 * singleTask + HOME intent filter makes this the home screen.
 *
 * Handles:
 * - Back button: collapses expanded tray / clears search, never finishes
 * - Physical keyboard: routes key presses to the search query
 */
class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Hide the system status bar — we draw our own
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            BBLauncherRoot(viewModel)
        }
    }

    /** Back collapses tray / clears search — never finishes the launcher. */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!viewModel.handleBack()) {
            // Don't call super — launcher should never finish
        }
    }

    /**
     * Physical keyboard input → type-to-search.
     * Printable characters are appended to the search query.
     * DEL key removes the last character.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return super.dispatchKeyEvent(event)

        // Handle DEL (backspace) — remove last char from search
        if (event.keyCode == KeyEvent.KEYCODE_DEL) {
            val current = viewModel.searchQuery.value
            if (current.isNotEmpty()) {
                viewModel.updateSearchQuery(current.dropLast(1))
                return true
            }
            return super.dispatchKeyEvent(event)
        }

        // Ignore non-printable keys (shift, ctrl, arrows, etc.)
        val char = event.unicodeChar.toChar()
        if (event.unicodeChar == 0 || char.isISOControl()) {
            return super.dispatchKeyEvent(event)
        }

        // Append printable character to search query & expand tray
        viewModel.updateSearchQuery(viewModel.searchQuery.value + char)
        viewModel.setTrayExpanded(true)
        return true
    }
}
