package id.rezyfr.quiet.domain.model

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

