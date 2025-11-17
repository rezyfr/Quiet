package id.rezyfr.quiet.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun QuietMain() {
    QuietTheme {
        val navHostController = rememberNavController()
    }
}