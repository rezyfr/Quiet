package id.rezyfr.quiet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import id.rezyfr.quiet.domain.RuleAction
import id.rezyfr.quiet.screen.action.ActionItem
import id.rezyfr.quiet.screen.picktime.DayRange

@Entity
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: List<String>,
    val keywords: List<String>,
    val dayRange: List<DayRange>? = null,
    val text: String,
    val action: String,
    val enabled: Boolean,
)
