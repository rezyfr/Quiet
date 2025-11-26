package id.rezyfr.quiet.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.rezyfr.quiet.data.dao.NotificationDao
import id.rezyfr.quiet.data.entity.NotificationEntity

@Database(
    entities = [NotificationEntity::class],
    version = 1,
    exportSchema = true
)
internal abstract class QuietDataBase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}