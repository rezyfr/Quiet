package id.rezyfr.quiet.domain

import id.rezyfr.quiet.screen.action.ActionItem
import id.rezyfr.quiet.screen.picktime.DayRange

data class Rule(
    val packageName: List<String>,
    val keywords: List<String>,
    val dayRange: List<DayRange>?,
    val text: String,
    val action: ActionItem,
    val enabled: Boolean
)

enum class RuleAction {
    MUTE, BLOCK, CUSTOM;

    override fun toString(): String {
        return this.name.lowercase()
    }
}
