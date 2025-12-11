package id.rezyfr.quiet.util

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import id.rezyfr.quiet.CHANNEL_BATCH_DELIVERY
import id.rezyfr.quiet.domain.model.BatchModel
import id.rezyfr.quiet.domain.model.Rule
import id.rezyfr.quiet.domain.model.TimeCriteria
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.domain.repository.BatchRepository
import id.rezyfr.quiet.domain.repository.RuleRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class BatchDeliveryWorker(
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val batchRepository: BatchRepository by inject()
    private val ruleRepository: RuleRepository by inject()

    override suspend fun doWork(): Result {
        val ruleId = inputData.getLong("ruleId", -1)
        Log.d("DEBUGISSUE BatchDeliveryWorker", "Rule: $ruleId")
        if (ruleId == -1L) return Result.failure()

        val rule = ruleRepository.getRule(ruleId) ?: return Result.success()

        // Get all batched notifications
        val items = batchRepository.getBatch(ruleId)
        Log.d("DEBUGISSUE BatchDeliveryWorker", "Rule: ${items.joinToString { it.id.toString() }}")
        if (items.isEmpty()) return Result.success()

        items.forEach { deliverSingleNotification(it) }

        // Clear after delivery
        batchRepository.clear(ruleId)

        // Reschedule next batching window
        scheduleNextWindow(rule)

        return Result.success()
    }

    private fun deliverSingleNotification(item: BatchModel) {
        val ctx = applicationContext
        val nm = NotificationManagerCompat.from(ctx)
        val icon = getAppItem(ctx.packageManager, listOf(item.packageName)).first().icon!!.toBitmap()

        val notif = NotificationCompat.Builder(ctx, CHANNEL_BATCH_DELIVERY)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("Content Title")
            .setContentText("Content Text")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Use timestamp or unique ID so notifications don’t overwrite each other
        Log.d("DEBUGISSUE BatchDeliveryWorker", "deliverSingleNotification: $item")
        val channels = nm.notificationChannels
        Log.d("Quiet", "Delivering title='${item.title}' text='${item.text}'")

        channels.forEach {
            Log.d("DEBUGISSUE Quiet", "Channel: ${it.id} importance=${it.importance}")
        }
        nm.notify(item.timestamp.toInt(), notif)
    }

    private fun scheduleNextWindow(rule: Rule) {
        val ctx = applicationContext
        val wm = WorkManager.getInstance(ctx)

        val ranges = rule.criteria
            .filterIsInstance<TimeCriteria>()
            .flatMap { it.ranges }

        ranges.forEach { range ->
            val delay = computeDelayUntil(range)
            if (delay <= 0) return@forEach

            val work = OneTimeWorkRequestBuilder<BatchDeliveryWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("ruleId" to rule.id))
                .build()

            wm.enqueue(work)
        }
    }

    private fun computeDelayUntil(range: TimeRange): Long {
        val now = LocalDateTime.now()

        val today = now.dayOfWeek
        val targetDay = range.day

        // how many days until next occurrence?
        var daysUntil = targetDay.ordinal - today.ordinal
        if (daysUntil < 0) daysUntil += 7 // next week

        // Target date
        val targetDate = now.toLocalDate().plusDays(daysUntil.toLong())

        // Target time (end of window)
        val endHour = range.endMinutes / 60
        val endMin = range.endMinutes % 60

        var targetDateTime = LocalDateTime.of(targetDate, LocalTime.of(endHour, endMin))

        // If today's window already ended, schedule next week's
        if (!targetDateTime.isAfter(now)) {
            targetDateTime = targetDateTime.plusDays(7)
        }

        return Duration.between(now, targetDateTime).toMillis()
    }

}
