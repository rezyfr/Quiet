package id.rezyfr.quiet.screen.rule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.rezyfr.quiet.component.QuietBottomSheet
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX

@Composable
fun CooldownTimeBottomSheet(
    modifier: Modifier = Modifier,
    items: List<Long>,
    onItemClick: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    QuietBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
    ) {
        CooldownTimeBottomSheetContent(
            modifier =
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer),
            items = items,
            onItemClick = onItemClick
        )
    }
}

@Composable
fun CooldownTimeBottomSheetContent(
    modifier: Modifier = Modifier,
    items: List<Long> = listOf(),
    onItemClick: (Long) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().padding(horizontal = spacingXX, vertical = spacingXX)
    ) {
        items(items) { item ->
            CooldownTimeItem(time = item, onClick = { onItemClick(item) })
            Spacer(Modifier.height(spacingX))
        }
    }
}

@Composable
fun CooldownTimeItem(modifier: Modifier = Modifier, time: Long, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Text(
            text = time.toText(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = spacingX, vertical = spacing)
        )
    }
}

fun Long.toText(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "$days days"
        hours > 0 -> "$hours hours"
        minutes > 0 -> "$minutes minutes"
        seconds > 0 -> "$seconds seconds"
        else -> "$this ms"
    }
}
