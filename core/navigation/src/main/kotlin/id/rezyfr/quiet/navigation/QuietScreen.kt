package id.rezyfr.quiet.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class QuietScreens(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    val name: String = route.appendArguments(navArguments)
    data object Welcome : QuietScreens("welcome")
    // home screen
    data object Home : QuietScreens("home")

    data object AddRules : QuietScreens("add_rules")

    data object PickApp : QuietScreens("pick_app")
    // example screen
    data object Example : QuietScreens(
        route = "example",
        navArguments = listOf(
            navArgument("exampleId") { type = NavType.StringType },
            /** navArgument("user") {
            type = WhatsAppUserType()
            nullable = false
            } **/
        )
    ) {
        fun createRoute(channelId: String) =
            name.replace("{${navArguments.first().name}}", channelId)
    }
}

private fun String.appendArguments(navArguments: List<NamedNavArgument>): String {
    val mandatoryArguments = navArguments.filter { it.argument.defaultValue == null }
        .takeIf { it.isNotEmpty() }
        ?.joinToString(separator = "/", prefix = "/") { "{${it.name}}" }
        .orEmpty()
    val optionalArguments = navArguments.filter { it.argument.defaultValue != null }
        .takeIf { it.isNotEmpty() }
        ?.joinToString(separator = "&", prefix = "?") { "${it.name}={${it.name}}" }
        .orEmpty()
    return "$this$mandatoryArguments$optionalArguments"
}