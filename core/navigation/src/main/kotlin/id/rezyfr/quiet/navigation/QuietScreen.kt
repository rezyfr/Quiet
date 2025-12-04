package id.rezyfr.quiet.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class QuietScreens(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList(),
) {
    val name: String = route.appendArguments(navArguments)

    data object Welcome : QuietScreens("welcome")

    // home screen
    data object Home : QuietScreens("home")

    data object AddRules : QuietScreens("add_rules")

    data object PickApp : QuietScreens(
        route = "pick_app",
        navArguments = listOf(
            navArgument("key_picked_apps") {
                type = NavType.StringType
                defaultValue = "[]"
            }
        )
    ) {
        const val KEY_PICKED_APPS = "key_picked_apps"

        fun createRoute(pickedApps: List<String>): String {
            val json = Json.encodeToString(pickedApps)
            return "pick_app?key_picked_apps=${Uri.encode(json)}"
        }
    }

    data object Criteria : QuietScreens(
        route = "criteria",
        navArguments = listOf(
            navArgument("key_picked_criteria") {
                type = NavType.StringType
                defaultValue = "[]"
            }
        )
    ) {
        const val KEY_CRITERIA = "key_picked_criteria"
        fun createRoute(pickedCriteria: List<String>): String {
            val json = Json.encodeToString(pickedCriteria)
            return "criteria?key_picked_criteria=${Uri.encode(json)}"
        }
    }

    data object Action : QuietScreens("actions")

    data object PickTime : QuietScreens("pick_time")
}

private fun String.appendArguments(navArguments: List<NamedNavArgument>): String {
    val mandatoryArguments =
        navArguments
            .filter { it.argument.defaultValue == null }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(separator = "/", prefix = "/") { "{${it.name}}" }
            .orEmpty()
    val optionalArguments =
        navArguments
            .filter { it.argument.defaultValue != null }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(separator = "&", prefix = "?") { "${it.name}={${it.name}}" }
            .orEmpty()
    return "$this$mandatoryArguments$optionalArguments"
}
