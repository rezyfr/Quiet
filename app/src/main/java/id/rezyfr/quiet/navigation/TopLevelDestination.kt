package id.rezyfr.quiet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val iconTextId: Int? = null,
)

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = QuietPage.Rules.route,
            icon = Icons.Default.Notifications,
            iconTextId = R.string.tab_rules,
        ),
        TopLevelDestination(
            icon = Icons.AutoMirrored.Filled.List,
            route = QuietPage.History.route,
            iconTextId = R.string.tab_history,
        ),
    )

sealed class QuietPage(val route: String, val index: Int) {
    object Rules : QuietPage("rule", index = 0)

    object History : QuietPage("history", index = 1)
}
