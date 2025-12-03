package id.rezyfr.quiet.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RuleAction

@Serializable
@SerialName("cooldown")
data class CooldownAction(
    val target: String,
    val durationMs: Long
) : RuleAction

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
