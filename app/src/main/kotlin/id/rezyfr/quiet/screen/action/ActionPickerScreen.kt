package id.rezyfr.quiet.screen.action

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.domain.model.CooldownAction
import id.rezyfr.quiet.domain.model.RuleAction
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX
import id.rezyfr.quiet.util.drawable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActionPickerScreen(viewModel: ActionPickerScreenViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ActionPickerContent(
        categories = state.actions,
        expandedState = state.expandedCategory,
        selectedAction = state.selectedAction,
        onToggleCategory = viewModel::expandCategory,
        onSelectAction = viewModel::onActionSelected,
        onPickAction = viewModel::pickAction,
    )
}

@Composable
fun ActionPickerContent(
    modifier: Modifier = Modifier,
    categories: List<ActionCategory> = listOf(),
    expandedState: Map<String, Boolean> = mapOf(),
    selectedAction: RuleAction? = null,
    onToggleCategory: (String) -> Unit = {},
    onSelectAction: (RuleAction) -> Unit = {},
    onPickAction: () -> Unit = {},
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = spacingXX)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = spacingXX),
        ) {
            categories.forEach { category ->
                item {
                    CategoryHeader(
                        name = category.name,
                        expanded = expandedState[category.name] == true,
                        onToggle = { onToggleCategory(category.name) },
                    )
                }

                item { Spacer(Modifier.height(spacingXX)) }
                if (expandedState[category.name] == true) {
                    items(category.items) { action ->
                        ActionCard(
                            item = action,
                            selected = selectedAction == action,
                            onClick = { onSelectAction(action) },
                        )
                        Spacer(Modifier.height(spacingX))
                    }
                }

                item { Spacer(Modifier.height(spacingXX)) }
            }
        }

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.action_pick),
            enabled = selectedAction != null,
            onClick = onPickAction,
        )

        Spacer(Modifier.height(spacingXX))
    }
}

@Composable
fun CategoryHeader(
    name: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
) {
    Row(
        modifier =
        modifier.fillMaxWidth().clickable(onClick = onToggle).padding(vertical = spacing),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.width(spacing))
        Icon(
            imageVector =
            if (expanded) {
                Icons.Default.KeyboardArrowUp
            } else {
                Icons.Default.KeyboardArrowDown
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Composable
fun ActionCard(
    item: RuleAction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    selected: Boolean = false,
) {
    val context = LocalContext.current
    val bg =
        if (selected) {
            MaterialTheme.colorScheme.onBackground
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    val contentColor =
        if (selected) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.onBackground
        }
    val iconColor = getActionColor(item.category)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(spacingX), horizontalAlignment = Alignment.Start) {
            // Icon
            Box(
                modifier =
                Modifier.size(48.dp)
                    .background(iconColor.first, RoundedCornerShape(12.dp))
                    .padding(spacing),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = item.icon.drawable(context),
                    contentDescription = null,
                    tint = iconColor.second,
                )
            }

            Spacer(Modifier.height(spacingX))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = contentColor,
            )

            Spacer(Modifier.height(spacingSmall))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                letterSpacing = 2.sp,
                lineHeight = 18.sp,
                color = contentColor.copy(alpha = 0.5f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewActionPicker() {
    QuietTheme {
        ActionPickerContent(
            expandedState = mapOf("Category 1" to true, "Category 2" to true),
            selectedAction = CooldownAction(
                title = "Action 1",
                description = "Description 1",
                icon = R.drawable.ic_launcher_foreground,
                durationMs = 1000,
                target = "app"
            ),
            categories =
            listOf(
                ActionCategory(
                    name = "Category 1",
                    id = "category_1",
                    items =
                    listOf(
                        CooldownAction(
                            title = "Action 1",
                            description = "Description 1",
                            icon = R.drawable.ic_launcher_foreground,
                            durationMs = 1000,
                            target = "app"
                        ),
                    ),
                ),
            ),
        )
    }
}
