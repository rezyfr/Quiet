package id.rezyfr.quiet.screen.picktime

import androidx.lifecycle.ViewModel
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek

class PickTimeViewModel(
    private val appComposeNavigator: AppComposeNavigator
) : ViewModel() {

    private val _state = MutableStateFlow(PickTimeState())
    val state = _state.asStateFlow()

    fun updateTimeRange(day: DayOfWeek, start: Int, end: Int) {
        _state.value = _state.value.copy(
            days = _state.value.days.map {
                if (it.day == day) {
                    it.copy(startMinutes = start, endMinutes = end)
                } else {
                    it
                }
            },
            isModified = true
        )
    }

    fun pickTime() {
        appComposeNavigator.navigateBackWithResult("key_pick_time", _state.value.days, QuietScreens.AddRules.route)
    }

    fun reset(day: DayOfWeek) {
        _state.update { state ->
            val day = state.days.find { it.day == day }
            state.copy(
                days = state.days.map {
                    if (it.day == day?.day) {
                        it.copy(startMinutes = 0, endMinutes = 0)
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun applyToAll(startMinutes: Int, endMinutes: Int) {
        _state.update { state ->
            state.copy(
                days = state.days.map {
                    it.copy(startMinutes = startMinutes, endMinutes = endMinutes)
                }
            )
        }
    }

    data class PickTimeState(
        val days: List<TimeRange> =
            DayOfWeek.entries.map {
                TimeRange(
                    day = it,
                    startMinutes = 0,
                    endMinutes = 0,
                )
            },
        val isModified: Boolean = false
    )
}
