package id.rezyfr.quiet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationEntity(
    @PrimaryKey(autoGenerate = false) val sbnKey: String,
    val packageName: String,
    val title: String,
    val text: String,
    val postTime: Long,
    var saved: Boolean,
)
