package id.rezyfr.quiet.data.repository

import id.rezyfr.quiet.data.entity.NotificationEntity
import id.rezyfr.quiet.domain.NotificationModel
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun saveNotification(notification: NotificationModel): Long

    fun getNotification(packageName: String?): Flow<List<NotificationEntity>>
}
