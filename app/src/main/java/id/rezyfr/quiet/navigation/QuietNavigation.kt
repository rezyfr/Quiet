package id.rezyfr.quiet.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import id.rezyfr.quiet.screen.action.ActionPickerScreen
import id.rezyfr.quiet.screen.criteria.CriteriaScreen
import id.rezyfr.quiet.screen.main.MainBottomPager
import id.rezyfr.quiet.screen.pickapp.PickAppScreen
import id.rezyfr.quiet.screen.picktime.PickTimeScreen
import id.rezyfr.quiet.screen.rule.AddRuleScreen

fun NavGraphBuilder.quietHomeNavigation() {
    composable(route = QuietScreens.Home.name) { MainBottomPager() }
}

fun NavGraphBuilder.addRuleNavigation(navHostController: NavHostController) {
    composable(route = QuietScreens.AddRules.route) { AddRuleScreen(navHostController) }

    composable(route = QuietScreens.PickApp.route) { PickAppScreen() }

    composable(route = QuietScreens.Criteria.route) { CriteriaScreen() }

    composable(route = QuietScreens.PickTime.route) { PickTimeScreen() }

    composable(route = QuietScreens.Action.route) { ActionPickerScreen() }
}
