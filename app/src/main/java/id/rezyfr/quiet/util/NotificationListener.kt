package id.rezyfr.quiet.util

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import id.rezyfr.quiet.data.repository.NotificationRepository
import id.rezyfr.quiet.data.repository.RuleRepository
import id.rezyfr.quiet.domain.model.BatchAction
import id.rezyfr.quiet.domain.model.CooldownAction
import id.rezyfr.quiet.domain.model.NotificationModel
import id.rezyfr.quiet.domain.model.Rule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationListener : NotificationListenerService(), KoinComponent {

    private val dataMap = HashMap<String, NotificationModel>()

    private val notificationRepository: NotificationRepository by inject()
    private val ruleRepository: RuleRepository by inject()
    private val coroutineScope: CoroutineScope by inject()

    override fun onListenerConnected() {
        try {
            activeNotifications.takeLast(30).map(::createNotificationItem).forEach {
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
        val pkg = sbn.packageName
        val title = sbn.notification.extras.getString("android.title") ?: ""
        val text = sbn.notification.extras.getString("android.text") ?: ""

        val content = "$title $text".lowercase()

        val rules = runBlocking {
            ruleRepository.getRules()
        }

        rules.forEach { rule ->
            val keywordMatch = rule.keywords.any { content.contains(it.lowercase()) }
            if (!keywordMatch) return@forEach
            /**
             *
            // 2. Check day
            if (rule.dayRange != null && rule.dayRange.isNotEmpty()) {
            val today = LocalDate.now().dayOfWeek
            rule.dayRange.forEach {
            if (today !in rule.dayRange) return@forEach
            }
            }

            // 3. Check time range
            val nowMinutes = LocalTime.now().hour * 60 + LocalTime.now().minute
            if (nowMinutes !in rule.startMinutes..rule.endMinutes) return@forEach
             */
            if (rule.enabled) {
                applyAction(rule, sbn) // CANCEL HERE IMMEDIATELY
            }
        }

        saveNotification(sbn)
    }

    private fun saveBulkNotification() {
        dataMap.forEach { (k, v) ->
            if (v.saved) return@forEach
            coroutineScope.launch {
                val saveOperation = notificationRepository.saveNotification(v)
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
            val saveOperation = notificationRepository.saveNotification(notificationItem)
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
            saved = false,
        )
    }

    fun applyAction(rule: Rule, sbn: StatusBarNotification) {
        when (rule.action) {
            is CooldownAction -> cancelNotification(sbn.key)
            is BatchAction -> cancelNotification(sbn.key)
            else -> { /* custom action */ }
        }
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
