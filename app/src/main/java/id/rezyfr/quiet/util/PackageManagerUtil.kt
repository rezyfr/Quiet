package id.rezyfr.quiet.util

import android.content.pm.PackageManager
import id.rezyfr.quiet.screen.pickapp.AppItem

fun getAppItem(pm: PackageManager, appPackageName: List<String>): List<AppItem> {
    return appPackageName.mapNotNull { packageName ->
        try {
            val info = pm.getApplicationInfo(packageName, 0)
            AppItem(
                label = info.loadLabel(pm).toString(),
                icon = info.loadIcon(pm),
                packageName = packageName,
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}
