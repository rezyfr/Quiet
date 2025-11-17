package id.rezyfr.quiet.util

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationListener : NotificationListenerService() {
    companion object {
        const val ACTION_REFRESH = "REFRESH"
        const val KEY_NOTIFICATIONS = "key_notifications"
    }

    //when user switches ON in Settings
    override fun onListenerConnected() {

    }
    //when user switches OFF in Settings
    override fun onListenerDisconnected() {

    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {

    }

}
