package id.rezyfr.quiet.screen.main.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.rezyfr.quiet.domain.model.Rule
import id.rezyfr.quiet.domain.repository.RuleRepository
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RulesScreenViewModel(private val navigator: AppComposeNavigator, private val rulesRepository: RuleRepository) : ViewModel() {

    private val _state = MutableStateFlow(RulesScreenState())
    val state = _state.asStateFlow()

    fun navigateToAddRules(ruleId: Long? = null) {
        navigator.navigate(QuietScreens.AddRules.createRoute(ruleId))
    }

    fun getRules() {
        viewModelScope.launch {
            rulesRepository.getAllRules().collect { rules ->
                _state.update {
                    if (rules.isNotEmpty()) {
                        it.copy(rules = ViewState.Success(rules))
                    } else {
                        it.copy(rules = ViewState.Empty)
                    }
                }
            }
        }
    }

    fun toggleRules(rule: Rule) {
        viewModelScope.launch {
            rulesRepository.updateRule(rule.copy(enabled = !rule.enabled))
        }
    }

    fun deleteRule(rule: Rule) {
        viewModelScope.launch {
            rulesRepository.deleteRule(rule)
        }
    }

    data class RulesScreenState(
        val rules: ViewState<List<Rule>> = ViewState.Empty
    )
}

sealed class ViewState<out T> {
    object Loading : ViewState<Nothing>()
    object Empty : ViewState<Nothing>()
    class Success<T>(val data: T?) : ViewState<T>()
    class Failure(val error: Throwable) : ViewState<Nothing>()
}
