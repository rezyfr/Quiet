package id.rezyfr.quiet.screen.rule

import androidx.lifecycle.ViewModel
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import id.rezyfr.quiet.screen.action.ActionItem
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

    fun setCriteria(criteria: List<String>) {
        _state.update { it.copy(criteriaText = criteria) }
    }

    fun setAction(action: ActionItem) {
        _state.update { it.copy(action = action) }
    }

    fun navigateToPickApp() {
        navigator.navigate(QuietScreens.PickApp.route)
    }

    fun navigateToPickCriteria() {
        navigator.navigate(QuietScreens.Criteria.route)
    }

    fun navigateToPickAction() {
        navigator.navigate(QuietScreens.Action.route)
    }

    data class AddRuleScreenState(
        val appItem: AppItem? = null,
        val criteriaText: List<String> = emptyList(),         // null -> "anything"
        val action: ActionItem? = null,
    )
}