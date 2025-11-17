package id.rezyfr.quiet

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import id.rezyfr.quiet.screen.welcome.WelcomeScreen
import id.rezyfr.quiet.ui.theme.QuietTheme

@Composable
fun App() {
    QuietTheme {
        val navController = rememberNavController()
        WelcomeScreen()
    }
}
