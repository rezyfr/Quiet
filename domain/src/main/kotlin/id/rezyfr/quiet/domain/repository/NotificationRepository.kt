package id.rezyfr.quiet.domain.repository

import id.rezyfr.quiet.domain.model.NotificationModel
import id.rezyfr.quiet.domain.model.TimeRange
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun saveNotification(notification: NotificationModel): Long

    fun getRecentNotifications(packageName: List<String>, phrases: List<String>, timeSpan: List<TimeRange>): Flow<List<NotificationModel>>
}
