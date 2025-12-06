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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.rezyfr.quiet.domain.model.CriteriaType
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX

@Composable
fun ExtraCriteriaBottomSheet(
    modifier: Modifier = Modifier,
    items: List<CriteriaType>,
    onItemClick: (CriteriaType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        ExtraCriteriaBottomSheetContent(
            modifier =
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer),
            items = items,
            onItemClick = onItemClick
        )
    }
}

@Composable
fun ExtraCriteriaBottomSheetContent(
    modifier: Modifier = Modifier,
    items: List<CriteriaType> = listOf(),
    onItemClick: (CriteriaType) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().padding(horizontal = spacingXX, vertical = spacingXX)
    ) {
        items(items) { item ->
            ExtraCriteriaItem(criteria = item, onClick = { onItemClick(item) })
            Spacer(Modifier.height(spacingX))
        }
    }
}

@Composable
fun ExtraCriteriaItem(modifier: Modifier = Modifier, criteria: CriteriaType, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Text(
            text = "filter by ${criteria.value}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = spacingX, vertical = spacing)
        )
    }
}