package id.rezyfr.quiet.util

import android.content.Context

class CooldownPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("cooldowns", Context.MODE_PRIVATE)

    fun get(ruleId: Long) = prefs.getLong(ruleId.toString(), 0L)

    fun set(ruleId: Long, timestamp: Long) {
        prefs.edit().putLong(ruleId.toString(), timestamp).apply()
    }
}