package id.rezyfr.quiet.ui.theme

import android.R.attr.type
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.rezyfr.quiet.domain.model.AttentionAction
import id.rezyfr.quiet.domain.model.DelayAction
import id.rezyfr.quiet.domain.model.RuleAction
import id.rezyfr.quiet.domain.model.SilenceAction

private val DarkBackground = Color(0xFF0a0b0d) // near-black
private val DarkCard = Color(0xFF1a1c21) // slightly lighter for cards
private val BlackText = Color(0xFF1a1c21)

private val YellowPrimary = Color(0xFFfff387) // warm muted yellow

private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF848485) // subtle gray
private val TextHint = Color(0xFFc4c4c7) // light gray for hints
private val TextFieldBackground = Color(0xFF3f414b) // dark gray for text fields

val SilenceBackground = Color(0xFF92d1f2)
val SilenceContent = Color(0xFF143055)
val AttentionContent = Color(0xFF531e1e)
val AttentionBackground = Color(0xFFff7070)
val DelayContent = Color(0xFF824941)
val DelayBackground = Color(0xFFff8b71)

val DismissContent = Color(0xFF4b3528)
val DismissBackground = YellowPrimary


private val DarkColorScheme =
    darkColorScheme(
        primary = YellowPrimary,
        onPrimary = BlackText,
        primaryContainer = YellowPrimary,
        onPrimaryContainer = BlackText,
        secondaryContainer = DarkCard,
        onSecondaryContainer = YellowPrimary,
        background = DarkBackground,
        onBackground = TextPrimary,
        surface = DarkCard,
        onSurface = TextPrimary,
        surfaceVariant = DarkCard,
        onSurfaceVariant = TextSecondary,
        tertiaryContainer = TextFieldBackground,
        onTertiaryContainer = TextPrimary,
        outline = Color(0xFF3A3A3A),
    )

val QuietShapes =
    Shapes(
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(18.dp),
        large = RoundedCornerShape(24.dp),
    )

val QuietTypography =
    Typography(
        headlineMedium =
        androidx.compose.ui.text.TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
        bodyLarge =
        androidx.compose.ui.text.TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
        bodyMedium =
        androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    )

@Composable
fun QuietTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = QuietTypography,
        shapes = QuietShapes,
        content = content,
    )
}
