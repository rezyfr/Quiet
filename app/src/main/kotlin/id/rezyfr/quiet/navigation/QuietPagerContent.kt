package id.rezyfr.quiet.navigation

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import id.rezyfr.quiet.screen.main.rules.RulesScreen

@SuppressLint("ContextCastToActivity")
@Composable
fun QuietPagerContent(modifier: Modifier = Modifier, page: Int = 0) {
    val activity = (LocalContext.current as? Activity)
    BackHandler { activity?.finish() }

    when (page) {
        QuietPage.Rules.index -> RulesScreen(modifier)
        QuietPage.History.index -> Box {}
    }
}
