package id.rezyfr.quiet.util

import id.rezyfr.quiet.domain.model.BatchAction
import id.rezyfr.quiet.domain.model.BluetoothCriteria
import id.rezyfr.quiet.domain.model.CallCriteria
import id.rezyfr.quiet.domain.model.CooldownAction
import id.rezyfr.quiet.domain.model.DismissAction
import id.rezyfr.quiet.domain.model.PostureCriteria
import id.rezyfr.quiet.domain.model.Rule
import id.rezyfr.quiet.domain.model.RuleAction
import id.rezyfr.quiet.domain.model.RuleCriteria
import id.rezyfr.quiet.domain.model.TimeCriteria

fun Rule.describe(): String {
    val appText = if (apps.isEmpty()) {
        "any app"
    } else {
        apps.joinToString(", ") { it.substringAfterLast('.') }
    }

    val keywordText = if (keywords.isEmpty() || (keywords.size == 1 && keywords.first().isBlank())) {
        "contains anything"
    } else {
        "contains " + keywords.joinToString(" or ") { "\"${it}\"" }
    }

    val criteriaText = if (criteria.isEmpty()) {
        ""
    } else {
        " " + criteria.joinToString(" and ") { it.describe() }
    }

    val actionText = action.describe()

    return "When I get a notification from $appText that $keywordText$criteriaText then $actionText"
}


fun RuleCriteria.describe(): String = when (this) {
    is TimeCriteria -> if (ranges == null) "any time" else "during schedule"
    is CallCriteria -> if (status == "on_call") "I'm on a call" else "I'm not on a call"
    is BluetoothCriteria ->
        if (mode == "any") "connected to any bluetooth device"
        else "connected to $deviceName"
    is PostureCriteria -> when (posture) {
        "in_pocket" -> "device is in pocket"
        "face_down" -> "device face down"
        else -> posture
    }
}
fun RuleAction.describe(): String = when (this) {

    is DismissAction ->
        if (immediately) "dismiss immediately"
        else "dismiss after ${delayMs?.div(1000)} seconds"

    is CooldownAction ->
        "mute for ${durationMs / 60000} minutes"

    is BatchAction ->
        if (schedule == null) "deliver in batch"
        else "deliver only during schedule"
}
