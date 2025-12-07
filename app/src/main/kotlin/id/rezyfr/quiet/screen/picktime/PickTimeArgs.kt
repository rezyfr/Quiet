package id.rezyfr.quiet.screen.picktime

import id.rezyfr.quiet.domain.model.TimeRange
import kotlinx.serialization.Serializable

@Serializable
data class PickTimeArgs(
    val timeRanges: List<TimeRange> = listOf(),
    val type: String = "time_criteria",
)