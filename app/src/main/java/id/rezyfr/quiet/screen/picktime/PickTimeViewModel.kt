package id.rezyfr.quiet.screen.picktime

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek

class PickTimeViewModel : ViewModel() {

    private val _state = MutableStateFlow(PickTimeState())
    val state = _state.asStateFlow()

    fun updateTimeRange(day: DayOfWeek, start: Int, end: Int) {
        _state.value = _state.value.copy(
            days = _state.value.days.map {
                if (it.day == day) it.copy(startMinutes = start, endMinutes = end)
                else it
            },
            isModified = true
        )
    }

    data class PickTimeState(
        val days: List<DayRange> =
            DayOfWeek.entries.map {
                DayRange(
                    day = it,
                    startMinutes = 0,
                    endMinutes = 1440,
                )
            },
        val isModified: Boolean = false
    )
}

data class DayRange(
    val day: DayOfWeek,  // or String if you prefer
    val startMinutes: Int,
    val endMinutes: Int
)