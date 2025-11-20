package id.rezyfr.quiet.screen.rule

import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddRuleScreenViewModel(
    private val navigator: AppComposeNavigator
) : ViewModel() {
    private val _state = MutableStateFlow(AddRuleScreenState())
    val state = _state.asStateFlow()

    fun navigateToPickApp() {
        navigator.navigate(QuietScreens.PickApp.route)
    }

    data class AddRuleScreenState(
        val appLabel: String? = null,             // null -> "any app"
        val appIcon: Painter? = null,             // optional app icon
        val criteriaText: String? = null,         // null -> "anything"
        val actionLabel: String = "do nothing",           // e.g. "do nothing", "mute"
        val actionIcon: Painter? = null    // optional action icon
    )
}