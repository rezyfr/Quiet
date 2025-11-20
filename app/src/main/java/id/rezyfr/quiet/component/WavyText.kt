package id.rezyfr.quiet.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.rezyfr.quiet.ui.theme.QuietTheme

@Composable
fun WavyText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {}
) {
    var textWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Column(modifier = modifier.clickable(true, onClick = onClick)) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            onTextLayout = { textLayoutResult ->
                with(density) {
                    textWidth = textLayoutResult.size.width.toDp()
                }
            }
        )

        WavyLine(modifier = Modifier.width(textWidth), color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun WavyLine(modifier: Modifier = Modifier, color: Color = Color.Black) {
    val infiniteTransition = rememberInfiniteTransition()
    val wave by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val showPoints by remember { mutableStateOf(false) }

    Canvas(
        modifier = modifier
            .height(15.dp)
    ) {
        val wavelength = 24.dp.toPx()
        val amplitude = 4.dp.toPx()
        val segment = wavelength / 4f
        val centerY = size.height / 2
        val points = mutableListOf<Offset>()
        val step = wave * wavelength
        var distance = 0f
        val path = Path().apply {
            reset()
            moveTo(step + 0f, centerY)
            points.add(Offset(step + 0f, centerY))
            while (distance < (size.width + wavelength)) {
                val x1 = segment + distance + step
                val x2 = segment * 2 + distance + step
                val x3 = segment * 3 + distance + step
                val x4 = segment * 4 + distance + step
                val y1 = centerY - amplitude
                val y2 = centerY
                val y3 = centerY + amplitude
                val y4 = centerY

                points.add(Offset(x1, y1))
                points.add(Offset(x2, y2))
                points.add(Offset(x3, y3))
                points.add(Offset(x4, y4))

                quadraticBezierTo(x1, y1, x2, y2)
                quadraticBezierTo(x3, y3, x4, y4)
                distance += wavelength
            }
        }

        clipRect {
            drawPath(path = path, color = color, style = Stroke(width = 12f, cap = StrokeCap.Round))
            if (showPoints) {
                points.forEach {
                    drawCircle(color = Color.Black, radius = 2f, center = it)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWavyText() {
    QuietTheme {
        Surface {
            WavyText(
                text = "Hello World"
            )
        }
    }
}