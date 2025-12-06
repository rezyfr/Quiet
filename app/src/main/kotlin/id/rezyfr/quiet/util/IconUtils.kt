package id.rezyfr.quiet.util

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun Int.drawable(context: Context): Painter {
    return rememberDrawablePainter(AppCompatResources.getDrawable(context, this))
}
