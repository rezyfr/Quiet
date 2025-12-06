package id.rezyfr.quiet.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.navigation.QuietScreens.Criteria.KEY_CRITERIA
import id.rezyfr.quiet.navigation.QuietScreens.PickApp.KEY_PICKED_APPS
import id.rezyfr.quiet.navigation.QuietScreens.PickTime.KEY_PICK_TIME
import id.rezyfr.quiet.screen.action.ActionPickerScreen
import id.rezyfr.quiet.screen.criteria.CriteriaScreen
import id.rezyfr.quiet.screen.main.MainBottomPager
import id.rezyfr.quiet.screen.pickapp.PickAppScreen
import id.rezyfr.quiet.screen.picktime.PickTimeScreen
import id.rezyfr.quiet.screen.rule.AddRuleScreen
import id.rezyfr.quiet.util.RuleJson.json

fun NavGraphBuilder.quietHomeNavigation() {
    composable(route = QuietScreens.Home.name) { MainBottomPager() }

}

fun NavGraphBuilder.addRuleNavigation(navHostController: NavHostController) {
    composable(route = QuietScreens.AddRules.name) { AddRuleScreen(navHostController) }

    composable(
        route = QuietScreens.PickApp.name,
        arguments = QuietScreens.PickApp.navArguments
    ) {
        val raw = it.arguments?.getString(KEY_PICKED_APPS) ?: "[]"
        val pickedApps = json.decodeFromString<List<String>>(raw)
        PickAppScreen(pickedApps)
    }

    composable(route = QuietScreens.Criteria.name) {
        val raw = it.arguments?.getString(KEY_CRITERIA) ?: "[]"
        val criteria = json.decodeFromString<List<String>>(raw)
        CriteriaScreen(
            phrases = criteria
        )
    }

    composable(route = QuietScreens.PickTime.name) {
        val raw = it.arguments?.getString(KEY_PICK_TIME) ?: "[]"
        val timeRange = json.decodeFromString<List<TimeRange>>(raw)
        PickTimeScreen(timeRange)
    }

    composable(route = QuietScreens.Action.name) { ActionPickerScreen() }
}
