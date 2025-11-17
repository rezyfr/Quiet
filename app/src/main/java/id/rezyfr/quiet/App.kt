package id.rezyfr.quiet

import androidx.compose.runtime.Composable
import id.rezyfr.quiet.screen.WelcomeScreen
import id.rezyfr.quiet.ui.theme.QuietTheme

@Composable
fun App() {
    QuietTheme {
        WelcomeScreen()
    }
}
