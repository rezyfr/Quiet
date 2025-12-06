package id.rezyfr.quiet.data.repository

import id.rezyfr.quiet.data.dao.NotificationDao
import id.rezyfr.quiet.data.entity.NotificationEntity
import id.rezyfr.quiet.domain.model.NotificationModel
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

class NotificationRepositoryImpl(val notificationDao: NotificationDao) : NotificationRepository {
    override suspend fun saveNotification(notification: NotificationModel): Long {
        if (notificationDao.count() > 30) {
            notificationDao.deleteOldNotifications()
        }
        return notificationDao.insertNotification(
            NotificationEntity(
                sbnKey = notification.sbnKey,
                packageName = notification.packageName,
                title = notification.title,
                text = notification.text,
                postTime = notification.postTime,
                saved = true,
            )
        )
    }

    override fun getRecentNotifications(packageName: List<String>, phrases: List<String>, timeSpan: List<TimeRange>): Flow<List<NotificationModel>> {
        return notificationDao.getRecentNotifications(
            packageName,
            packageName.isEmpty(),
            phrases.joinToString(" "),
            phrases.isNotEmpty()
        ).map { list ->
            if (timeSpan.isEmpty()) return@map list.take(10).toModel()

            list.filter { notif ->
                val notifDay = notif.dayOfWeek()
                val notifMinutes = notif.toLocalMinutes()

                timeSpan.any { range ->
                    range.day == notifDay &&
                        notifMinutes in range.startMinutes..range.endMinutes
                }
            }.take(10)
                .map {
                NotificationModel(
                   it.sbnKey,
                   it.packageName,
                   it.title,
                   it.text,
                   it.postTime,
                   it.saved,
                )
            }
        }
    }

    fun NotificationEntity.toLocalMinutes(): Int {
        val date = Instant.ofEpochMilli(postTime)
            .atZone(ZoneId.systemDefault())
        return date.hour * 60 + date.minute
    }

    fun NotificationEntity.dayOfWeek(): DayOfWeek {
        return Instant.ofEpochMilli(postTime)
            .atZone(ZoneId.systemDefault())
            .dayOfWeek
    }

    fun List<NotificationEntity>.toModel(): List<NotificationModel> {
        return this.map {
            NotificationModel(
                it.sbnKey,
                it.packageName,
                it.title,
                it.text,
                it.postTime,
                it.saved,
            )
        }
    }
}
