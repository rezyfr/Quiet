package id.rezyfr.quiet.util

import android.app.Notification
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import id.rezyfr.quiet.domain.model.BatchAction
import id.rezyfr.quiet.domain.model.BatchModel
import id.rezyfr.quiet.domain.model.CooldownAction
import id.rezyfr.quiet.domain.model.DismissAction
import id.rezyfr.quiet.domain.model.NotificationModel
import id.rezyfr.quiet.domain.model.Rule
import id.rezyfr.quiet.domain.model.TimeCriteria
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.domain.repository.BatchRepository
import id.rezyfr.quiet.domain.repository.NotificationRepository
import id.rezyfr.quiet.domain.repository.RuleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NotificationListener() : NotificationListenerService(), KoinComponent {

    private val dataMap = HashMap<String, NotificationModel>()
    private val notificationRepository: NotificationRepository by inject()
    private val ruleRepository: RuleRepository by inject()
    private val batchRepository: BatchRepository by inject()
    private val coroutineScope: CoroutineScope by inject()
    private val context: Context by inject()
    private lateinit var cooldownPrefs: CooldownPrefs

    override fun onCreate() {
        super.onCreate()
        cooldownPrefs = CooldownPrefs(this)
    }

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
        val title = sbn.notification.extras.getString("android.title") ?: ""
        val text = sbn.notification.extras.getString("android.text") ?: ""
        val content = "$title $text".lowercase()

        val rules = runBlocking {
            ruleRepository.getRules()
        }

        rules.forEach { rule ->
            if (!rule.enabled) return@forEach

            // 1. Keyword match
            val keywordMatch = if(rule.keywords.isEmpty()) true else rule.keywords.any { content.contains(it.lowercase()) }
            if (!keywordMatch) return@forEach

            // 2. Time range match
            if (rule.criteria.any { it is TimeCriteria }) {
                val timeMatch = matchesTimeRange(sbn, (rule.criteria.find { it is TimeCriteria} as TimeCriteria).ranges)
                if (!timeMatch) return@forEach
            }

            applyAction(rule, sbn)
        }

        saveNotification(sbn)
    }

    private fun StatusBarNotification.toLocalDateTime(): LocalDateTime {
        return Instant.ofEpochMilli(this.postTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    private fun StatusBarNotification.dayOfWeek(): DayOfWeek {
        return this.toLocalDateTime().dayOfWeek
    }

    private fun StatusBarNotification.minutesSinceMidnight(): Int {
        val dt = this.toLocalDateTime()
        return dt.hour * 60 + dt.minute
    }

    private fun matchesTimeRange(
        sbn: StatusBarNotification,
        ranges: List<TimeRange>
    ): Boolean {
        if (ranges.isEmpty()) return true

        val notifDay = sbn.dayOfWeek()
        val notifMinutes = sbn.minutesSinceMidnight()

        return ranges.any { range ->
            range.day == notifDay &&
                notifMinutes in range.startMinutes..range.endMinutes
        }
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

    private fun isInCooldown(rule: Rule): Boolean {
        val now = System.currentTimeMillis()
        return now < cooldownPrefs.get(rule.id)
    }

    fun isInBatchWindow(windows: List<TimeRange>): Boolean {
        val now = LocalDateTime.now()
        val day = now.dayOfWeek
        val minutes = now.hour * 60 + now.minute

        return windows.any { it.day == day && minutes in it.startMinutes until it.endMinutes }
    }

    fun applyAction(rule: Rule, sbn: StatusBarNotification) {
        when (rule.action) {
            is CooldownAction -> {
                if (isInCooldown(rule)) {
                    cancelNotification(sbn.key)
                } else {
                    val duration = (rule.action as CooldownAction).durationMs
                    val nextAllowed = System.currentTimeMillis() + duration
                    cooldownPrefs.set(rule.id, nextAllowed)
                }
            }
            is DismissAction -> cancelNotification(sbn.key)
            is BatchAction -> {
                val action = rule.action as BatchAction
                if (action.schedule.isNotEmpty()) {
                    if (isInBatchWindow(action.schedule)){
                        Log.d("DEBUGISSUE NotificationListener", "Rule ${rule.id}: isInBatchWindow ${isInBatchWindow(action.schedule)}")
                        coroutineScope.launch {
                            batchRepository.addBatch(
                                BatchModel(
                                    ruleId = rule.id,
                                    packageName = sbn.packageName,
                                    title = "Test ${rule.id}}".trim(),
                                    text = sbn.getText(),
                                    timestamp = System.currentTimeMillis(),
                                    id = 0
                                )
                            )
                        }
                        cancelNotification(sbn.key)
                        BatchScheduler.scheduleForRule(context, rule)
                    }
                }
            }
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
