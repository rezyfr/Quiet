package id.rezyfr.quiet.data.repository

import id.rezyfr.quiet.data.entity.NotificationEntity
import id.rezyfr.quiet.domain.model.NotificationModel
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun saveNotification(notification: NotificationModel): Long

    fun getRecentNotifications(packageName: List<String>, phrases: List<String>): Flow<List<NotificationEntity>>
}
