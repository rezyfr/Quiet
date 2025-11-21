package id.rezyfr.quiet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.rezyfr.quiet.screen.pickapp.PickAppScreen
import id.rezyfr.quiet.screen.rule.AddRuleScreen
import id.rezyfr.quiet.screen.welcome.WelcomeScreen
import id.rezyfr.quiet.util.isIgnoringBatteryOptimizations
import id.rezyfr.quiet.util.isNotificationAccessGranted
import id.rezyfr.quiet.util.isNotificationAllowed

@Composable
fun QuietNavHost(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val startDestination = if(isNotificationAccessGranted(context) && isNotificationAllowed(context) && isIgnoringBatteryOptimizations(context)) {
        QuietScreens.Home.route
    } else {
        QuietScreens.Welcome.route
    }

    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable(route = QuietScreens.Welcome.route) {
            WelcomeScreen()
        }

        quietHomeNavigation()

        composable(route = QuietScreens.AddRules.route) {
            AddRuleScreen(
                navHostController
            )
        }

        composable(route = QuietScreens.PickApp.route) {
            PickAppScreen()
        }
    }
}