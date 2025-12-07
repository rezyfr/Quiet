package id.rezyfr.quiet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BatchEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleId: Long,
    val title: String,
    val text: String,
    val packageName: String,
    val timestamp: Long
)