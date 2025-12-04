package id.rezyfr.quiet.data.repository

import id.rezyfr.quiet.data.dao.NotificationDao
import id.rezyfr.quiet.data.entity.NotificationEntity
import id.rezyfr.quiet.domain.model.NotificationModel
import kotlinx.coroutines.flow.Flow

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

    override fun getRecentNotifications(packageName: List<String>, phrases: List<String>): Flow<List<NotificationEntity>> {
        return notificationDao.getRecentNotifications(
            packageName,
            packageName.isEmpty(),
            phrases.joinToString(" "),
            phrases.isNotEmpty()
        )
    }
}
