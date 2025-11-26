package id.rezyfr.quiet.util

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import id.rezyfr.quiet.data.repository.NotificationRepository
import id.rezyfr.quiet.domain.NotificationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationListener : NotificationListenerService(), KoinComponent {

    private val dataMap = HashMap<String, NotificationModel>()

    private val repository: NotificationRepository by inject()
    private val coroutineScope: CoroutineScope by inject()

    override fun onListenerConnected() {
        try {
            activeNotifications
                .takeLast(30)
                .map(::createNotificationItem)
                .forEach {
                    dataMap[it.sbnKey] = it
                }
            saveBulkNotification()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onListenerDisconnected() {
        dataMap.clear()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        saveNotification(sbn)
    }

    private fun saveBulkNotification() {
        dataMap.forEach { (k, v) ->
            if (v.saved) return@forEach
            coroutineScope.launch {
                val saveOperation = repository.saveNotification(v)
                if (saveOperation != -1L) {
                    dataMap[k] = v.copy(saved = true)
                }
            }
        }
    }

    private fun saveNotification(sbn: StatusBarNotification) {
        val notificationItem = createNotificationItem(sbn)
        dataMap[sbn.key] = notificationItem
        coroutineScope.launch {
            val saveOperation = repository.saveNotification(notificationItem)
            if (saveOperation != -1L) {
                dataMap[sbn.key] = notificationItem.copy(saved = true)
            }
        }
    }

    private fun createNotificationItem(sbn: StatusBarNotification): NotificationModel {
        return NotificationModel(
            sbn.key,
            sbn.packageName,
            title = "${sbn.getTitleBig()}\n${sbn.getTitle()}".trim(),
            text = sbn.getText(),
            postTime = sbn.postTime,
            saved = false
        )
    }
}

fun StatusBarNotification.getText(): String =
    (this.notification.extras.get(Notification.EXTRA_TEXT) ?: "").toString()

fun StatusBarNotification.getTitle(): String =
    (this.notification.extras.get(Notification.EXTRA_TITLE) ?: "").toString()

fun StatusBarNotification.getTitleBig(): String =
    (this.notification.extras.get(Notification.EXTRA_TITLE_BIG) ?: "").toString()

fun StatusBarNotification.getSubText(): String =
    (this.notification.extras.get(Notification.EXTRA_SUB_TEXT) ?: "").toString()