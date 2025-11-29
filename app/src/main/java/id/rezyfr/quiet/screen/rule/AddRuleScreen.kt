package id.rezyfr.quiet.screen.rule

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastJoinToString
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.component.WavyText
import id.rezyfr.quiet.domain.ExtraCriteria
import id.rezyfr.quiet.domain.ExtraCriteriaType
import id.rezyfr.quiet.domain.NotificationUiModel
import id.rezyfr.quiet.screen.action.ActionItem
import id.rezyfr.quiet.screen.pickapp.AppItem
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX
import id.rezyfr.quiet.util.drawable
import id.rezyfr.quiet.util.getAppItem
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddRuleScreen(
    navController: NavController,
    viewModel: AddRuleScreenViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val appPackageName = backStackEntry?.savedStateHandle?.get<String>("key_pick_apps")
    val pickedApp = remember(appPackageName) { getAppItem(pm, appPackageName.orEmpty()) }
    var showExtraSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getRecentNotification(pm, null)
        viewModel.getExtraCriteria()
    }

    LaunchedEffect(appPackageName) {
        if (appPackageName != null) {
            backStackEntry.savedStateHandle.remove<String>("key_pick_apps")
            viewModel.setAppItem(pickedApp)
            viewModel.getRecentNotification(pm, pickedApp?.packageName)
        }
    }
    val criteria = backStackEntry?.savedStateHandle?.get<List<String>>("key_criteria")

    LaunchedEffect(criteria) {
        if (criteria != null) {
            backStackEntry.savedStateHandle.remove<List<String>>("key_criteria")
            viewModel.setCriteria(criteria)
        }
    }
    val actionString = backStackEntry?.savedStateHandle?.get<String>("key_pick_actions")

    LaunchedEffect(actionString) {
        if (actionString != null) {
            try {
                val action = Json.decodeFromString<ActionItem>(actionString)
                backStackEntry.savedStateHandle.remove<String>("key_pick_actions")
                viewModel.setAction(action)
            } catch (e: Exception) {
                // Optionally log the error or show an error message
                backStackEntry.savedStateHandle.remove<String>("key_pick_actions")
            }
        }
    }
    val state by viewModel.state.collectAsState()

    AddRuleContent(
        onAppClick = { viewModel.navigateToPickApp() },
        onCriteriaClick = { viewModel.navigateToPickCriteria() },
        onActionClick = { viewModel.navigateToPickAction() },
        onExtraCriteriaClick = { id ->
            when (id) {
                ExtraCriteriaType.TIME -> {
                    viewModel.navigateToPickTime()
                }
                ExtraCriteriaType.CALL_STATUS -> TODO()
            }
        },
        onAddExtraCriteriaClick = { showExtraSheet = true },
        state = state,
    )

    if (showExtraSheet) {
        ExtraCriteriaBottomSheet(
            items = state.extraCriteriaList,
            onItemClick = { criteria ->
                showExtraSheet = false
                viewModel.addExtraCriteria(criteria)
            },
            onDismiss = { showExtraSheet = false }
        )
    }
}

@Composable
fun AddRuleContent(
    state: AddRuleScreenViewModel.AddRuleScreenState,
    modifier: Modifier = Modifier,
    onAppClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    onCriteriaClick: () -> Unit = {},
    onAddExtraCriteriaClick: () -> Unit = {},
    onExtraCriteriaClick: (ExtraCriteriaType) -> Unit = {},
) {
    Surface(modifier.background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(spacingX),
            contentPadding = PaddingValues(bottom = spacingXX),
        ) {
            // ----- HEADER -----
            item {
                RuleEditorHeader(
                    state = state,
                    onAppClick = onAppClick,
                    onCriteriaClick = onCriteriaClick,
                    onActionClick = onActionClick,
                    onAddExtraCriteriaClick = onAddExtraCriteriaClick,
                    onExtraCriteriaClick = onExtraCriteriaClick,
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            // ----- SAVE BUTTON -----
            item {
                PrimaryButton(
                    text = stringResource(R.string.rule_save),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSaveClick,
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                )
            }

            // ----- RECENT MATCHING -----
            item {
                RecentMatchingNotificationsSection(
                    modifier = Modifier.fillMaxWidth(),
                    notifications = state.notificationList,
                )
            }
        }
    }
}

@Composable
private fun ExtraCriteriaText(
    extraCriteriaText: List<ExtraCriteria>,
    onExtraCriteriaClick: (ExtraCriteriaType) -> Unit = {},
    onAddExtraCriteriaClick: () -> Unit = {}
) {
    if (extraCriteriaText.isEmpty()) return
    extraCriteriaText.forEachIndexed { index, criteria ->
        if (index != 0) {
            Text(" and")
        }
        Row {
            Text(" ")
            WavyText("${criteria.description}", onClick = { onExtraCriteriaClick(criteria.id) })
            if (index == extraCriteriaText.lastIndex) {
                AddExtraCriteria(onAddExtraCriteriaClick = onAddExtraCriteriaClick)
            }
        }
        Spacer(Modifier.width(spacing))
    }
}

@Composable
fun RuleEditorHeader(
    state: AddRuleScreenViewModel.AddRuleScreenState,
    modifier: Modifier = Modifier,
    onAppClick: () -> Unit = {},
    onCriteriaClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    onAddExtraCriteriaClick: () -> Unit = {},
    onExtraCriteriaClick: (ExtraCriteriaType) -> Unit = {}
) {
    val appText = state.appItem?.label ?: stringResource(R.string.rule_any_app)
    val containsText =
        if (state.criteriaText.isEmpty()) {
            stringResource(R.string.rule_contains_anything)
        } else {
            "contains ${
                state.criteriaText.fastJoinToString(" or ") {
                    "\"${it.capitalize()}\""
                }
            }"
        }

    FlowRow(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
        ) {
            Text(
                stringResource(R.string.rule_when_notification),
                Modifier.padding(bottom = spacingX)
            )
            RuleApps(state, state.appItem?.icon, appText, onAppClick)
            RuleCriteria(containsText, onCriteriaClick)
            if (state.selectedExtraCriteria.isEmpty()) {
                AddExtraCriteria(onAddExtraCriteriaClick = onAddExtraCriteriaClick)
            }
            RuleExtraCriteria(state.selectedExtraCriteria, onExtraCriteriaClick)
            RuleActions(state, onActionClick)
        }
    }
}

@Composable
private fun AddExtraCriteria(onAddExtraCriteriaClick: () -> Unit) {
    Spacer(Modifier.width(spacing))
    Box(
        Modifier.background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(8.dp)
        )
            .clickable(onClick = onAddExtraCriteriaClick)
    ) {
        Text(
            text = "+",
            modifier =
            Modifier.padding(horizontal = 8.dp, vertical = spacingSmall)
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun RuleCriteria(containsText: String, onCriteriaClick: () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        WavyText(text = containsText, onClick = onCriteriaClick)
    }
}

@Composable
private fun RuleExtraCriteria(
    extraCriteria: List<ExtraCriteria>,
    onExtraCriteriaClick: (ExtraCriteriaType) -> Unit = {},
    onAddExtraCriteriaClick: () -> Unit = {}
) {
    FlowRow(verticalArrangement = Arrangement.Top) {
        ExtraCriteriaText(
            extraCriteriaText = extraCriteria,
            onExtraCriteriaClick = onExtraCriteriaClick,
            onAddExtraCriteriaClick = onAddExtraCriteriaClick
        )
    }
}

@Composable
private fun RuleApps(
    state: AddRuleScreenViewModel.AddRuleScreenState,
    icon: Drawable?,
    appText: String,
    onAppClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.Top) {
        Text(stringResource(R.string.from))

        if (state.appItem?.icon != null) {
            Icon(
                painter = rememberDrawablePainter(icon),
                contentDescription = null,
                tint = Color.Unspecified, // show original icon color
                modifier = Modifier.size(22.dp).clip(CircleShape),
            )
            Spacer(Modifier.width(6.dp))
        }

        WavyText(text = appText, onClick = onAppClick)

        Text(" that")
    }
}

@Composable
private fun RuleActions(
    state: AddRuleScreenViewModel.AddRuleScreenState,
    onActionClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.Top) {
        Text(stringResource(R.string.then))

        if (state.action?.icon != null) {
            val context = LocalContext.current
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = state.action.icon.drawable(context),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Spacer(Modifier.width(6.dp))
        }

        WavyText(
            text = state.action?.title ?: stringResource(R.string.rule_do_nothing),
            onClick = onActionClick,
        )
    }
}

@Composable
fun RecentMatchingEmptySection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text(
            text = stringResource(R.string.rule_recent_matching_notifications),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.rule_no_recent_notifications_match),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun RecentMatchingNotificationsSection(
    notifications: List<Pair<NotificationUiModel, AppItem>>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier =
            Modifier.fillMaxWidth()
                .height(1.5.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        )

        Spacer(Modifier.height(spacingXX))

        if (notifications.isEmpty()) {
            RecentMatchingEmptySection()
        } else {
            notifications.forEach { item ->
                RecentNotificationCard(item = item)
                Spacer(Modifier.height(spacingX))
            }
        }

        Spacer(Modifier.height(spacingXX))
    }
}

@Composable
fun RecentNotificationCard(
    item: Pair<NotificationUiModel, AppItem>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(spacingX).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Row: App Icon + App Name + Channel + Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = rememberDrawablePainter(item.second.icon),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp),
                    )

                    Spacer(Modifier.width(spacingSmall))

                    Text(
                        text = item.second.label,
                        style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = item.first.postTime.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(spacing))
                // Title
                Text(
                    text = item.first.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(spacingSmall))
                // Body message preview
                Text(
                    text = item.first.text,
                    style =
                    MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun ExtraCriteriaBottomSheet(
    modifier: Modifier = Modifier,
    items: List<ExtraCriteria>,
    onItemClick: (ExtraCriteria) -> Unit,
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
    items: List<ExtraCriteria> = listOf(),
    onItemClick: (ExtraCriteria) -> Unit = {}
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
fun ExtraCriteriaItem(modifier: Modifier = Modifier, criteria: ExtraCriteria, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Text(
            text = "filter by ${criteria.label}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = spacingX, vertical = spacing)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExtraCriteriaBottomSheetPreview() {
    QuietTheme { ExtraCriteriaBottomSheetContent(items = ExtraCriteria.DEFAULT) }
}

@Preview(showBackground = true)
@Composable
private fun AddRuleEmptyPreview() {
    QuietTheme {
        AddRuleContent(
            state = AddRuleScreenViewModel.AddRuleScreenState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddRuleFilledPreview() {
    val context = LocalContext.current
    QuietTheme {
        AddRuleContent(
            state =
            AddRuleScreenViewModel.AddRuleScreenState(
                appItem =
                AppItem(
                    label = "Microsoft Teams",
                    packageName = "com.microsoft.teams",
                    icon =
                    AppCompatResources.getDrawable(
                        context, R.drawable.ic_launcher_foreground
                    )!!,
                ),
                criteriaText = listOf("meeting", "call"),
                selectedExtraCriteria = ExtraCriteria.DEFAULT,
                action = null,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddRuleFilledWithRecentPreview() {
    val context = LocalContext.current
    QuietTheme {
        AddRuleContent(
            state =
            AddRuleScreenViewModel.AddRuleScreenState(
                appItem =
                AppItem(
                    label = "Microsoft Teams",
                    packageName = "com.microsoft.teams",
                    icon =
                    AppCompatResources.getDrawable(
                        context, R.drawable.ic_launcher_foreground
                    )!!,
                ),
                criteriaText = listOf("meeting", "call"),
                action = null,
                notificationList =
                listOf(
                    Pair(
                        NotificationUiModel(
                            sbnKey = "sbnKey",
                            packageName = "com.btpn.dc",
                            title = "Streaming Makin Hemat",
                            text =
                            "Karena ada cashback 50% untuk bayar layanan steaming favorit pakai Kartu Kredit Jenius. Khusus buat kamu, Cek di sini\uD83D\uDC47\uD83C\uDFFD\n",
                            postTime = "07:00 AM",
                        ),
                        AppItem(
                            label = "Jenius",
                            packageName = "com.btpn.dc",
                            icon =
                            AppCompatResources.getDrawable(
                                context, R.drawable.ic_launcher_foreground
                            )!!,
                        ),
                    )
                ),
            ),
        )
    }
}
