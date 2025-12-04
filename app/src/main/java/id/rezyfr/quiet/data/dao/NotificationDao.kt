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
    WHERE 
        (:isEmpty = 1 OR packageName IN (:packages))
        AND (
            :hasPhrases = 0 OR (
                title LIKE '%' || :phrase || '%' 
                OR text LIKE '%' || :phrase || '%'
            )
        )
    ORDER BY postTime DESC
    LIMIT 10
    """
    )
    fun getRecentNotifications(
        packages: List<String>,
        isEmpty: Boolean,
        phrase: String?,    // <— merged into one search string
        hasPhrases: Boolean // <— is phrase empty?
    ): Flow<List<NotificationEntity>>

    @Query(
        "DELETE FROM notificationentity where sbnKey NOT IN (SELECT sbnKey from notificationentity ORDER BY sbnKey DESC LIMIT 30)"
    )
    suspend fun deleteOldNotifications()

    @Query("SELECT COUNT(sbnKey) FROM notificationentity") fun count(): Int
}
