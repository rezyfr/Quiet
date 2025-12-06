package id.rezyfr.quiet.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RuleCriteria

@Serializable
@SerialName("time")
data class TimeCriteria(
    val ranges: List<TimeRange>
) : RuleCriteria

@Serializable
@SerialName("call_status")
data class CallCriteria(
    val status: String // "on_call" | "not_on_call"
) : RuleCriteria

@Serializable
@SerialName("bluetooth")
data class BluetoothCriteria(
    val mode: String, // any | specific
    val deviceName: String? = null
) : RuleCriteria

@Serializable
@SerialName("posture")
data class PostureCriteria(
    val posture: String // "in_pocket" | "face_down" | "face_up" | etc.
) : RuleCriteria

enum class CriteriaType(val value: String) {
    TIME("time"),
    CALL("call"),
    BLUETOOTH("bluetooth"),
    POSTURE("posture");
}

fun getCriteriaTypes(selected: List<RuleCriteria>) = CriteriaType.entries.toMutableList().apply {
    selected.forEach {
        if (it is TimeCriteria) remove(CriteriaType.TIME)
        if (it is CallCriteria) remove(CriteriaType.CALL)
        if (it is BluetoothCriteria) remove(CriteriaType.BLUETOOTH)
        if (it is PostureCriteria) remove(CriteriaType.POSTURE)
    }
}