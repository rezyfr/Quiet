package id.rezyfr.quiet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
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

private val DarkBackground = Color(0xFF0E0E0E)       // near-black
private val DarkSurface = Color(0xFF1A1A1A)          // soft dark gray
private val DarkCard = Color(0xFF191A1C)             // slightly lighter for cards

private val YellowPrimary = Color(0xFFFFE68A)        // warm muted yellow
private val YellowPressed = Color(0xFFFFD95C)         // slightly darker
private val YellowContainer = Color(0xFFFFF387)       // pale yellow container

private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFCECECE)         // subtle gray
private val TextMuted = Color(0xFF9A9A9A)

private val DarkColorScheme = darkColorScheme(
    primary = YellowPrimary,
    onPrimary = Color.Black,
    primaryContainer = YellowContainer,
    onPrimaryContainer = Color.Black,
    secondaryContainer = DarkCard,
    onSecondaryContainer = YellowPrimary,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,

    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,

    outline = Color(0xFF3A3A3A),
)

val QuietShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp)
)

val QuietTypography = Typography(
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
    )
)

@Composable
fun QuietTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = QuietTypography,
        shapes = QuietShapes,
        content = content
    )
}