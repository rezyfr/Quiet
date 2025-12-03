package id.rezyfr.quiet.domain.model

import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable
data class TimeRange(
    val day: DayOfWeek,
    val startMinutes: Int,
    val endMinutes: Int
)