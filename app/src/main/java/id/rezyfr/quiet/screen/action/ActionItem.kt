package id.rezyfr.quiet.screen.action

import androidx.annotation.DrawableRes
import kotlinx.serialization.Serializable

@Serializable
data class ActionItem(
    val id: String,
    val title: String,
    val description: String,
    @DrawableRes val icon: Int,
)

data class ActionCategory(val name: String, val items: List<ActionItem>)
