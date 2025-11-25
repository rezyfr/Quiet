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
import id.rezyfr.quiet.ui.theme.IconBlueBackground
import id.rezyfr.quiet.ui.theme.IconBlueContent
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX
import id.rezyfr.quiet.util.drawable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActionPickerScreen(
    viewModel: ActionPickerScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ActionPickerContent(
        categories = state.actions,
        expandedState = state.expandedCategory,
        selectedActionId = state.selectedAction?.id,
        onToggleCategory = viewModel::expandCategory,
        onSelectAction = viewModel::onActionSelected,
        onPickAction = viewModel::pickAction
    )
}

@Composable
fun ActionPickerContent(
    categories: List<ActionCategory> = listOf(),
    expandedState: Map<String, Boolean> = mapOf(),
    selectedActionId: String? = null,
    onToggleCategory: (String) -> Unit = {},
    onSelectAction: (ActionItem) -> Unit = {},
    onPickAction: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = spacingXX)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = spacingXX)
        ) {
            categories.forEach { category ->
                item {
                    CategoryHeader(
                        name = category.name,
                        expanded = expandedState[category.name] == true,
                        onToggle = { onToggleCategory(category.name) }
                    )
                }

                item {
                    Spacer(Modifier.height(spacingXX))
                }
                if (expandedState[category.name] == true) {
                    items(category.items) { action ->
                        ActionCard(
                            item = action,
                            selected = selectedActionId == action.id,
                            onClick = { onSelectAction(action) }
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
            enabled = selectedActionId != null,
            onClick = onPickAction
        )

        Spacer(Modifier.height(spacingXX))
    }
}
@Composable
fun CategoryHeader(
    name: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = spacing),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.width(spacing))
        Icon(
            imageVector =
                if (expanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}
@Composable
fun ActionCard(
    item: ActionItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val bg = if (selected) MaterialTheme.colorScheme.onBackground
    else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (selected) MaterialTheme.colorScheme.surfaceVariant
    else MaterialTheme.colorScheme.onBackground

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(spacingX),
            horizontalAlignment = Alignment.Start
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        IconBlueBackground,
                        RoundedCornerShape(12.dp)
                    ).padding(spacing),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = item.icon.drawable(context),
                    contentDescription = null,
                    tint = IconBlueContent
                )
            }

            Spacer(Modifier.height(spacingX))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor
            )

            Spacer(Modifier.height(spacingSmall))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                letterSpacing = 2.sp,
                lineHeight = 18.sp,
                color = contentColor.copy(alpha = 0.5f)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewActionPicker() {
    val context = LocalContext.current
    QuietTheme {
        ActionPickerContent(
            expandedState = mapOf(
                "Category 1" to true,
                "Category 2" to true,
            ),
            selectedActionId = "delete",
            categories = listOf(
                ActionCategory(
                    name = "Category 1",
                    items = listOf(
                        ActionItem(
                            id = "mute",
                            title = "Action 1",
                            description = "Description 1",
                            icon = R.drawable.ic_launcher_foreground
                        ),
                        ActionItem(
                            id = "unmute",
                            title = "Action 2",
                            description = "Description 2",
                            icon = R.drawable.ic_launcher_foreground
                        )
                    )
                ),
                ActionCategory(
                    name = "Category 2",
                    items = listOf(
                        ActionItem(
                            id = "delete",
                            title = "Action 3",
                            description = "Description 3",
                            icon = R.drawable.ic_launcher_foreground
                        ),
                        ActionItem(
                            id = "add",
                            title = "Action 4",
                            description = "Description 4",
                            icon = R.drawable.ic_launcher_foreground
                        )
                    )
                )
            )
        )
    }
}
