package id.rezyfr.quiet.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RuleAction {
    val title: String
    val description: String
    val icon: Int
    val category: String
}
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
    val durationMs: Long,
    override val title: String = "",
    override val description: String = "",
    override val icon: Int = -1,
    override val category: String = "silence"
) : SilenceAction

@Serializable
@SerialName("dismiss")
data class DismissAction(
    val immediately: Boolean,
    val delayMs: Long?,
    override val title: String = "",
    override val description: String = "",
    override val icon: Int = -1,
    override val category: String = "dismiss"
) : RuleAction

@Serializable
@SerialName("batch")
data class BatchAction(
    val mode: String, // schedule, interval
    val schedule: List<TimeRange>,
    override val title: String = "",
    override val description: String = "",
    override val icon: Int = -1,
    override val category: String = "delay"
) : RuleAction
