package id.rezyfr.quiet.screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class WelcomeViewModel : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val state : StateFlow<UiState> get() = _state

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