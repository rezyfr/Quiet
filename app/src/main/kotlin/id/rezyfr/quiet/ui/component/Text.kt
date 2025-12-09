package id.rezyfr.quiet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import id.rezyfr.quiet.ui.theme.AttentionBackground
import id.rezyfr.quiet.ui.theme.QuietTheme
import me.saket.extendedspans.ExtendedSpanPainter
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
fun Builder.withSquigglyError(text: String, onClick: () -> Unit = {}) {
    val spanStyle = SpanStyle(
        textDecoration = TextDecoration.Underline,
        color = AttentionBackground
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
    modifier: Modifier = Modifier,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    ),
    lineHeight: TextUnit = 52.sp,
    painter: ExtendedSpanPainter = squigglyPainterDefaults(),
) {
    val extendedSpans = remember {
        ExtendedSpans(painter)
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

@Composable
fun squigglyPainterDefaults(
    width: TextUnit = 4.sp,
    wavelength: TextUnit = 20.sp,
    amplitude: TextUnit = 2.sp,
    bottomOffset: TextUnit = 2.sp,
): SquigglyUnderlineSpanPainter {
    val underlineAnimator = rememberSquigglyUnderlineAnimator()
    return SquigglyUnderlineSpanPainter(
        width = width,
        wavelength = wavelength,
        amplitude = amplitude,
        bottomOffset = bottomOffset,
        animator = underlineAnimator,
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewExtendedText() {
    QuietTheme {
        Box(Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            ExtendedSpansText(
                text = buildAnnotatedString {
                    withSquiggly("Text")
                },
            )
        }
    }
}