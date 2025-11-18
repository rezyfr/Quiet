package id.rezyfr.quiet.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class WelcomeViewModel(
    private val composeNavigator: AppComposeNavigator
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val state : StateFlow<UiState> get() = _state.onEach {
        if (it.backgroundAllowed && it.notificationAllowed && it.enabled) {
            composeNavigator.navigate(QuietScreens.Home.route)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    fun checkBackground() {
        _state.update {
            it.copy(backgroundAllowed = true)
        }
    }

    fun checkNotification() {
        _state.update {
            it.copy(notificationAllowed = true)
        }
    }

    fun enableQuiet() {
        _state.update {
            it.copy(enabled = true)
        }
    }

    data class UiState(
        val backgroundAllowed: Boolean = false,
        val notificationAllowed: Boolean = false,
        val enabled: Boolean = false
    )
}