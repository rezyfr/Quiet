package id.rezyfr.quiet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietNavHost
import id.rezyfr.quiet.screen.welcome.WelcomeScreen
import id.rezyfr.quiet.ui.theme.QuietTheme

@Composable
fun App(
    composeNavigator: AppComposeNavigator
) {
    QuietTheme {
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            composeNavigator.handleNavigationCommands(navController)
        }

        QuietNavHost(navController)
    }
}
