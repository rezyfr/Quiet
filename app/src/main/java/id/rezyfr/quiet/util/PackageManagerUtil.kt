package id.rezyfr.quiet.util

import android.content.pm.PackageManager
import id.rezyfr.quiet.screen.pickapp.AppItem

fun getAppItem(pm: PackageManager, appPackageName: String): AppItem? {
    return try {
        val info = pm.getApplicationInfo(appPackageName, 0)
        AppItem(
            label = info.loadLabel(pm).toString(),
            icon = info.loadIcon(pm),
            packageName = appPackageName,
        )
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}
