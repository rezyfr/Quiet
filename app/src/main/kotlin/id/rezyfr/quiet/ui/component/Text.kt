package id.rezyfr.quiet.ui.component

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import me.saket.extendedspans.ExtendedSpans
import me.saket.extendedspans.SquigglyUnderlineSpanPainter
import me.saket.extendedspans.drawBehind
import me.saket.extendedspans.rememberSquigglyUnderlineAnimator

@Composable
fun Builder.withSquiggly(text: String, onClick: () -> Unit = {}) {
    val spanStyle = SpanStyle(
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colorScheme.primary
    )
    withLink(
        LinkAnnotation.Clickable(tag = "tag", linkInteractionListener = {
            onClick()
        })
    ) {
        withStyle(spanStyle) {
            append(text)
        }
    }
}


@Composable
fun ExtendedSpansText(
    text: AnnotatedString,
    inlineContent : Map<String, InlineTextContent> = mapOf(),
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    ),
    lineHeight: TextUnit = 52.sp,
    bottomOffset: TextUnit = 2.sp,
    modifier: Modifier = Modifier,
) {
    val underlineAnimator = rememberSquigglyUnderlineAnimator()
    val extendedSpans = remember {
        ExtendedSpans(
            SquigglyUnderlineSpanPainter(
                width = 4.sp,
                wavelength = 20.sp,
                amplitude = 2.sp,
                bottomOffset = bottomOffset,
                animator = underlineAnimator
            )
        )
    }

    BasicText(
        inlineContent = inlineContent,
        modifier = modifier.drawBehind(extendedSpans),
        onTextLayout = {
            extendedSpans.onTextLayout(it)
        },
        text = remember(text) {
            extendedSpans.extend(text)
        },
        style = textStyle.copy(
            lineHeight = lineHeight
        )
    )
}