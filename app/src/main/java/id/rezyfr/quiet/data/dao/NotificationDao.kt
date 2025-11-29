package id.rezyfr.quiet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rezyfr.quiet.data.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query(
        """
    SELECT * FROM notificationentity
    WHERE (:packageName IS NULL OR packageName = :packageName)
    ORDER BY postTime DESC LIMIT 10
"""
    )
    fun getRecentNotifications(packageName: String?): Flow<List<NotificationEntity>>

    @Query(
        "DELETE FROM notificationentity where sbnKey NOT IN (SELECT sbnKey from notificationentity ORDER BY sbnKey DESC LIMIT 30)"
    )
    suspend fun deleteOldNotifications()

    @Query("SELECT COUNT(sbnKey) FROM notificationentity") fun count(): Int
}
