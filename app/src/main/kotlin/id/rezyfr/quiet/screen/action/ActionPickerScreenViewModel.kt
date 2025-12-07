package id.rezyfr.quiet.screen.action

import androidx.lifecycle.ViewModel
import id.rezyfr.quiet.R
import id.rezyfr.quiet.domain.model.BatchAction
import id.rezyfr.quiet.domain.model.CooldownAction
import id.rezyfr.quiet.domain.model.DismissAction
import id.rezyfr.quiet.domain.model.RuleAction
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ActionPickerScreenViewModel(private val navigator: AppComposeNavigator) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun onActionSelected(action: RuleAction) {
        _state.update { it.copy(selectedAction = action) }
    }

    fun expandCategory(categoryName: String) {
        _state.update {
            it.copy(
                expandedCategory =
                it.expandedCategory.toMutableMap().apply {
                    put(categoryName, !getOrDefault(categoryName, false))
                }
            )
        }
    }

    fun pickAction() {
        val action = _state.value.selectedAction ?: return
        val serializedAction = Json.encodeToString(action)
        navigator.navigateBackWithResult(
            "key_pick_actions",
            serializedAction,
            QuietScreens.AddRules.route,
        )
    }

    data class State(
        val selectedAction: RuleAction? = null,
        val expandedCategory: Map<String, Boolean> =
            mapOf(
                "Silence actions" to true,
                "Dismiss actions" to true,
                "Automation actions" to false
            ),
        val actions: List<ActionCategory> =
            listOf(
                ActionCategory(
                    name = "Silence actions",
                    id = "silence",
                    items = listOf(
                        CooldownAction(
                            title = "Cooldown",
                            icon = R.drawable.ic_action_freeze,
                            description = "Prevent the same app or conversation from buzzing you multiple times in quick succession by muting or dismissing them automatically.",
                            target = "app",
                            durationMs = 300000
                        )
                    )
                ),
                ActionCategory(
                    name = "Dismiss actions",
                    id = "dismiss",
                    items = listOf(
                        DismissAction(
                            title = "Dismiss",
                            icon = R.drawable.ic_action_dismiss,
                            description = "Automatically dismiss the notification",
                            immediately = true,
                            delayMs = 0
                        )
                    )
                ),
                ActionCategory(
                    name = "Batch actions",
                    id = "batch",
                    items = listOf(
                        BatchAction(
                            title = "Batch",
                            icon = R.drawable.ic_action_batch,
                            description = "Receive notification in batch. Set specific time window to start and receive notification in batch.",
                            mode = "schedule",
                            schedule = listOf()
                        )
                    )
                ),
            ),
    )
}
