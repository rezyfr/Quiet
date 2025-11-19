package id.rezyfr.quiet.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import id.rezyfr.quiet.screen.main.MainBottomPager

fun NavGraphBuilder.quietHomeNavigation() {
    composable(route = QuietScreens.Home.name) {
        MainBottomPager()
    }
}

