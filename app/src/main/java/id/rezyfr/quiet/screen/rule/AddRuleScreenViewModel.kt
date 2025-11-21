package id.rezyfr.quiet.screen.rule

import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import id.rezyfr.quiet.screen.pickapp.AppItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddRuleScreenViewModel(
    private val navigator: AppComposeNavigator
) : ViewModel() {
    private val _state = MutableStateFlow(AddRuleScreenState())
    val state = _state.asStateFlow()

    fun setAppItem(appItem: AppItem?) {
        _state.update { it.copy(appItem = appItem) }
    }

    fun navigateToPickApp() {
        navigator.navigate(QuietScreens.PickApp.route)
    }

    data class AddRuleScreenState(
        val appItem: AppItem? = null,
        val criteriaText: String? = null,         // null -> "anything"
        val actionLabel: String = "do nothing",           // e.g. "do nothing", "mute"
        val actionIcon: Painter? = null    // optional action icon
    )
}