package id.rezyfr.quiet.screen.action

import androidx.compose.ui.graphics.Color
import id.rezyfr.quiet.domain.model.RuleAction
import id.rezyfr.quiet.ui.theme.AttentionBackground
import id.rezyfr.quiet.ui.theme.AttentionContent
import id.rezyfr.quiet.ui.theme.DelayBackground
import id.rezyfr.quiet.ui.theme.DelayContent
import id.rezyfr.quiet.ui.theme.DismissBackground
import id.rezyfr.quiet.ui.theme.DismissContent
import id.rezyfr.quiet.ui.theme.SilenceBackground
import id.rezyfr.quiet.ui.theme.SilenceContent

data class ActionCategory(
    val name: String,
    val items: List<RuleAction>,
    val id: String
)

fun getActionColor(id: String) : Pair<Color, Color> {
    return when(id) {
        "silence" -> Pair(SilenceBackground, SilenceContent)
        "attention" -> Pair(AttentionBackground, AttentionContent)
        "dismiss" -> Pair(DismissBackground, DismissContent)
        else -> Pair(DelayBackground, DelayContent)
    }
}
