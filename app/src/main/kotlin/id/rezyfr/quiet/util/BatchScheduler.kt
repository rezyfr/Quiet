package id.rezyfr.quiet.util

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import id.rezyfr.quiet.domain.model.Rule
import id.rezyfr.quiet.domain.model.TimeCriteria
import id.rezyfr.quiet.domain.model.TimeRange
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object BatchScheduler {

    fun scheduleForRule(context: Context, rule: Rule) {
        val wm = WorkManager.getInstance(context)

        rule.criteria
            .filterIsInstance<TimeCriteria>()
            .flatMap { it.ranges }
            .forEach { range ->
                val delay = computeDelayUntil(range)
                if (delay > 0) {
                    val work = OneTimeWorkRequestBuilder<BatchDeliveryWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(
                            workDataOf("ruleId" to rule.id)
                        )
                        .build()

                    wm.enqueue(work)
                }
            }
    }

    private fun computeDelayUntil(range: TimeRange): Long {
        val now = LocalDateTime.now()
        val today = now.dayOfWeek
        var targetDay = range.day

        var daysUntil = targetDay.ordinal - today.ordinal
        if (daysUntil < 0) daysUntil += 7 // next week

        val targetDate = now.plusDays(daysUntil.toLong()).toLocalDate()
        val targetTime = LocalTime.of(range.endMinutes / 60, range.endMinutes % 60)

        val targetDateTime = LocalDateTime.of(targetDate, targetTime)

        return Duration.between(now, targetDateTime).toMillis()
    }
}