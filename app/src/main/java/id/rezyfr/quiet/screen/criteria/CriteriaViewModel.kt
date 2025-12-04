package id.rezyfr.quiet.screen.criteria

import androidx.lifecycle.ViewModel
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CriteriaViewModel(private val navigator: AppComposeNavigator) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun navigateToAddRules() {
        navigator.navigateBackWithResult(
            "key_criteria",
            _state.value.phrase,
            QuietScreens.AddRules.route,
        )
    }

    fun addPhrase(phrase: String) {
        _state.update { it.copy(phrase = it.phrase + phrase) }
    }

    fun removePhrase(phrase: String) {
        _state.update { it.copy(phrase = it.phrase - phrase) }
    }

    fun setPhrases(phrases: List<String>) {
        _state.update { it.copy(phrase = phrases) }
    }

    data class State(val phrase: List<String> = emptyList())
}
