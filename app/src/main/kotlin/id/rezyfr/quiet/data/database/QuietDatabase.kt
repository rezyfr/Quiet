package id.rezyfr.quiet.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import id.rezyfr.quiet.data.dao.BatchDao
import id.rezyfr.quiet.data.dao.NotificationDao
import id.rezyfr.quiet.data.dao.RuleDao
import id.rezyfr.quiet.data.entity.BatchEntry
import id.rezyfr.quiet.data.entity.NotificationEntity
import id.rezyfr.quiet.data.entity.RuleEntity
import id.rezyfr.quiet.domain.model.TimeRange
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(entities = [NotificationEntity::class, RuleEntity::class, BatchEntry::class], version = 1, exportSchema = true,)
@TypeConverters(Converters::class)
internal abstract class QuietDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun ruleDao(): RuleDao
    abstract fun batchDao(): BatchDao
}


class Converters {
    @TypeConverter
    fun fromListString(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toListString(value: String?): List<String>? {
        return value?.split(",")
    }

    @TypeConverter
    fun fromDayRanges(value: List<TimeRange>?): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toDayRanges(value: String?): List<TimeRange>? {
        return Json.decodeFromString(value.orEmpty())
    }

    @TypeConverter
    fun fromDayRange(value: TimeRange): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toDayRange(value: String): TimeRange {
        return Json.decodeFromString(value)
    }
}