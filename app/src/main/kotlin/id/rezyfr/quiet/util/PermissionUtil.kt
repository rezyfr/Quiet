package id.rezyfr.quiet.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri

fun isNotificationAccessGranted(context: Context): Boolean {
    val enabledListeners =
        Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")

    return enabledListeners?.contains(context.packageName) == true
}

fun isNotificationAllowed(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

fun requestDisableBatteryOptimization(
    context: Context,
    fallbackLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
) {
    if (isIgnoringBatteryOptimizations(context)) return

    try {
        val intent =
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = "package:${context.packageName}".toUri()
            }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Some OEMs might not support this – open settings list instead
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        fallbackLauncher?.launch(intent)
    }
}

fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}
