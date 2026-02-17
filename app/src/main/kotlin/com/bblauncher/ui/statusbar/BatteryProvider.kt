package com.bblauncher.ui.statusbar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Provides a live battery percentage via a sticky broadcast receiver.
 * The returned [State] updates whenever Android broadcasts battery changes.
 */
@Composable
fun rememberBatteryLevel(): State<Int> {
    val context = LocalContext.current
    val level = remember { mutableIntStateOf(getBatteryLevel(context)) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val pct = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                if (pct >= 0) {
                    level.intValue = (pct * 100) / scale
                }
            }
        }
        context.registerReceiver(
            receiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        )

        onDispose { context.unregisterReceiver(receiver) }
    }

    return level
}

/** Read battery level synchronously from the sticky intent. */
private fun getBatteryLevel(context: Context): Int {
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}
