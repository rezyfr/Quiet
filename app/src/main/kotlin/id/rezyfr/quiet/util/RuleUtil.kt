package id.rezyfr.quiet.util

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import id.rezyfr.quiet.R
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
import id.rezyfr.quiet.screen.action.getActionColor

@Composable
fun inlineAppIcon(
    icon: Drawable?,
    size: Int = 24
): InlineTextContent {
    return InlineTextContent(
        placeholder = Placeholder(
            height = size.sp, // roughly width of the word
            width = size.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        Icon(
            painter = rememberDrawablePainter(icon),
            contentDescription = null,
            tint = Color.Unspecified, // show original icon color
            modifier = Modifier.size(size.dp).clip(CircleShape),
        )
    }
}

@Composable
fun inlineActionIcon(
    action: RuleAction,
    size: Int = 24
): InlineTextContent {
    val iconColor = getActionColor(action.category)
    return InlineTextContent(
        placeholder = Placeholder(
            height = size.sp, // roughly width of the word
            width = size.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        Box(
            modifier =
            Modifier.size(size.dp)
                .background(iconColor.first, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = action.icon.drawable(LocalContext.current),
                contentDescription = null,
                tint = iconColor.second,
                modifier = Modifier.size((size * 5/6).dp).clip(CircleShape),
            )
        }
    }
}

@Composable
fun Rule.describe(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    packageManager: PackageManager
) {
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

    val inlineContent = mutableMapOf<String, InlineTextContent>().apply {
        if (apps.size == 1) {
            val appInfo = getAppItem(packageManager, listOf(apps.first()))
            put("app_icon", inlineAppIcon(appInfo.first().icon, 20))
        }

        put("action_icon", inlineActionIcon(action, 20))
    }
    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.rule_when_notification))
        if (apps.size == 1) {
            append(" ")
            appendInlineContent("app_icon", "icon")
        }
        append(" $appText")
        append(" that ")
        append(keywordText)
        append(criteriaText)
        append(" then ")
        appendInlineContent("action_icon", "icon")
        append(" ")
        append(actionText)
    }

    AnimatedStrikethroughText(
        modifier = modifier,
        text = annotatedString,
        inlineContent = inlineContent,
        isVisible = !isEnabled
    )
}

@Composable
fun AnimatedStrikethroughText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    animateOnHide: Boolean = true,
    spec: AnimationSpec<Int> = tween(text.length * 5, easing = FastOutLinearInEasing),
    strikethroughStyle: SpanStyle = SpanStyle(),
    inlineContent: MutableMap<String, InlineTextContent>
) {
    var textToDisplay by remember { mutableStateOf(AnnotatedString("")) }

    val length = remember { Animatable(initialValue = 0, typeConverter = Int.VectorConverter) }

    LaunchedEffect(length.value) {
        textToDisplay = text.buildStrikethrough(length.value, strikethroughStyle)
    }

    LaunchedEffect(isVisible) {
        when {
            isVisible -> length.animateTo(text.length, spec)
            !isVisible && animateOnHide -> length.animateTo(0, spec)
            else -> length.snapTo(0)
        }
    }

    LaunchedEffect(text) {
        when {
            isVisible && text.length == length.value -> {
                textToDisplay = text.buildStrikethrough(length.value, strikethroughStyle)
            }
            isVisible && text.length != length.value -> {
                length.snapTo(text.length)
            }
            else -> textToDisplay = text
        }
    }

    BasicText(
        inlineContent = inlineContent,
        text = textToDisplay,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground),
        modifier = modifier
    )
}

private fun AnnotatedString.buildStrikethrough(length: Int, style: SpanStyle) = buildAnnotatedString {
    append(this@buildStrikethrough)
    val lineThroughStyle = style.copy(textDecoration = TextDecoration.LineThrough)
    addStyle(lineThroughStyle, 0, length)
}

fun RuleCriteria.describe(): String = when (this) {
    is TimeCriteria -> if (ranges.isEmpty()) "at any time" else "during schedule"
    is CallCriteria -> if (status == "on_call") "I'm on a call" else "I'm not on a call"
    is BluetoothCriteria ->
        if (mode == "any") {
            "connected to any bluetooth device"
        } else {
            "connected to $deviceName"
        }
    is PostureCriteria -> when (posture) {
        "in_pocket" -> "device is in pocket"
        "face_down" -> "device face down"
        else -> posture
    }
}

fun RuleAction.describe(): String = when (this) {
    is DismissAction ->
        if (immediately) {
            "dismiss immediately"
        } else {
            "dismiss after ${delayMs?.div(1000)} seconds"
        }

    is CooldownAction ->
        "mute for ${durationMs / 60000} minutes"

    is BatchAction ->
        if (schedule == null) {
            "deliver in batch"
        } else {
            "deliver only during schedule"
        }
}
