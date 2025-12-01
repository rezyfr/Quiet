package id.rezyfr.quiet.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import id.rezyfr.quiet.data.dao.NotificationDao
import id.rezyfr.quiet.data.dao.RuleDao
import id.rezyfr.quiet.data.entity.NotificationEntity
import id.rezyfr.quiet.data.entity.RuleEntity
import id.rezyfr.quiet.screen.picktime.DayRange
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(entities = [NotificationEntity::class, RuleEntity::class], version = 1, exportSchema = true,)
@TypeConverters(Converters::class)
internal abstract class QuietDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun ruleDao(): RuleDao
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
    fun fromDayRanges(value: List<DayRange>?): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toDayRanges(value: String?): List<DayRange>? {
        return Json.decodeFromString(value.orEmpty())
    }

    @TypeConverter
    fun fromDayRange(value: DayRange): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toDayRange(value: String): DayRange {
        return Json.decodeFromString(value)
    }
}