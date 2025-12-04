package id.rezyfr.quiet.domain.model

import androidx.compose.ui.graphics.Color
import id.rezyfr.quiet.ui.theme.AttentionBackground
import id.rezyfr.quiet.ui.theme.AttentionContent
import id.rezyfr.quiet.ui.theme.DelayBackground
import id.rezyfr.quiet.ui.theme.DelayContent
import id.rezyfr.quiet.ui.theme.DismissBackground
import id.rezyfr.quiet.ui.theme.DismissContent
import id.rezyfr.quiet.ui.theme.SilenceBackground
import id.rezyfr.quiet.ui.theme.SilenceContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RuleAction
@Serializable
sealed interface SilenceAction : RuleAction
@Serializable
sealed interface AttentionAction : RuleAction
@Serializable
sealed interface DelayAction : RuleAction

@Serializable
@SerialName("cooldown")
data class CooldownAction(
    val target: String,
    val durationMs: Long
) : SilenceAction

@Serializable
@SerialName("dismiss")
data class DismissAction(
    val immediately: Boolean,
    val delayMs: Long?
) : RuleAction

@Serializable
@SerialName("batch")
data class BatchAction(
    val mode: String,
    val schedule: List<TimeRange>?
) : RuleAction


fun getColor(action: RuleAction) : Pair<Color, Color> {
    return when(action) {
        is SilenceAction -> Pair(SilenceContent, SilenceBackground)
        is AttentionAction -> Pair(AttentionContent, AttentionBackground)
        is DelayAction -> Pair(DelayContent, DelayBackground)
        else -> Pair(DismissContent, DismissBackground)
    }
}
