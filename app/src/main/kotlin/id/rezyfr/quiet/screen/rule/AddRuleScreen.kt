package id.rezyfr.quiet.screen.rule

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.domain.model.BluetoothCriteria
import id.rezyfr.quiet.domain.model.CallCriteria
import id.rezyfr.quiet.domain.model.NotificationUiModel
import id.rezyfr.quiet.domain.model.PostureCriteria
import id.rezyfr.quiet.domain.model.RuleCriteria
import id.rezyfr.quiet.domain.model.TimeCriteria
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.domain.model.getCriteriaTypes
import id.rezyfr.quiet.screen.action.ActionItem
import id.rezyfr.quiet.screen.pickapp.AppItem
import id.rezyfr.quiet.ui.component.ExtendedSpansText
import id.rezyfr.quiet.ui.component.withSquiggly
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX
import id.rezyfr.quiet.util.describe
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
    var showCriteriaPicker by remember { mutableStateOf(false) }

    val appPackageName = backStackEntry?.savedStateHandle?.get<List<String>>("key_pick_apps")
    val criteria = backStackEntry?.savedStateHandle?.get<List<String>>("key_criteria")
    val actionString = backStackEntry?.savedStateHandle?.get<String>("key_pick_actions")
    val timeCriteria = backStackEntry?.savedStateHandle?.get<String>("key_pick_time")
    val pickedApp = remember(appPackageName) { getAppItem(pm, appPackageName.orEmpty()) }

    LaunchedEffect(appPackageName) {
        if (appPackageName != null) {
            backStackEntry.savedStateHandle.remove<List<String>>("key_pick_apps")
            viewModel.setAppItem(pickedApp)
        }
    }

    LaunchedEffect(criteria) {
        if (criteria != null) {
            backStackEntry.savedStateHandle.remove<List<String>>("key_criteria")
            viewModel.setCriteria(criteria)
        }
    }

    LaunchedEffect(appPackageName, criteria, timeCriteria) {
        viewModel.getRecentNotification(pm)
    }

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
    LaunchedEffect(timeCriteria) {
        if (timeCriteria != null) {
            try {
                val timeRange = Json.decodeFromString<List<TimeRange>>(timeCriteria)
                if (timeRange.isNotEmpty()) {
                    viewModel.addTimeCriteria(timeRange)
                }
                backStackEntry.savedStateHandle.remove<String>("key_pick_time")
            } catch (e: Exception) {
                // Optionally log the error or show an error message
                backStackEntry.savedStateHandle.remove<String>("key_pick_time")
            }
        }
    }

    val state by viewModel.state.collectAsState()

    AddRuleContent(
        onAppClick = { viewModel.navigateToPickApp() },
        onCriteriaClick = { viewModel.navigateToPickCriteria() },
        onActionClick = { viewModel.navigateToPickAction() },
        onExtraCriteriaClick = { criteria ->
            when (criteria) {
                is TimeCriteria -> {
                    viewModel.navigateToPickTime()
                }
                is BluetoothCriteria -> TODO()
                is CallCriteria -> TODO()
                is PostureCriteria -> TODO()
            }
        },
        onAddExtraCriteriaClick = { showCriteriaPicker = true },
        onSaveClick = {
            viewModel.saveRule()
        },
        state = state,
    )

    if (showCriteriaPicker) {
        ExtraCriteriaBottomSheet(
            items = viewModel.getAvailableCriteria(),
            onItemClick = { criteria ->
                showCriteriaPicker = false
                viewModel.addCriteria(criteria)
            },
            onDismiss = { showCriteriaPicker = false }
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
    onExtraCriteriaClick: (RuleCriteria) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(spacingX),
        contentPadding = PaddingValues(bottom = spacingXX),
    ) {
        item {
            Spacer(Modifier.height(spacingX))
        }
        // ----- HEADER -----
        item {
            RuleEditorHeader(
                state = state,
                onAppClick = onAppClick,
                onCriteriaClick = onCriteriaClick,
                onActionClick = onActionClick,
                onAddExtraCriteriaClick = onAddExtraCriteriaClick,
                onExtraCriteriaClick = onExtraCriteriaClick
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

@Composable
fun RuleEditorHeader(
    state: AddRuleScreenViewModel.AddRuleScreenState,
    modifier: Modifier = Modifier,
    onAppClick: () -> Unit = {},
    onCriteriaClick: () -> Unit = {},
    onAddExtraCriteriaClick: () -> Unit = {},
    onExtraCriteriaClick: (RuleCriteria) -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    val appText = if (state.selectedApps.size == 1) {
        state.selectedApps.first().label
    } else if (state.selectedApps.isEmpty()) {
        "any app"
    } else {
        state.selectedApps.joinToString(" or ") {
            it.label
        }
    }

    val containsText = if (state.criteriaText.isEmpty()) {
        "contains anything"
    } else {
        "contains " + state.criteriaText.joinToString(" or ") { "\"${it.capitalize()}\"" }
    }

    val inlineContent = mutableMapOf(
        "plus" to inlinePlus {},
    ).apply {
        if (state.selectedApps.size == 1) {
            put("app_icon", inlineAppIcon(state.selectedApps.first().icon))
        }
    }

    ExtendedSpansText(
        modifier = modifier.padding(vertical = 16.dp),
        inlineContent = inlineContent,
        bottomOffset = 8.sp,
        lineHeight = 60.sp,
        text = buildAnnotatedString {
            append(stringResource(R.string.rule_when_notification))
            if (state.selectedApps.size == 1) {
                appendInlineContent("app_icon", "icon")
            }
            withSquiggly(" $appText", onAppClick)
            append(" that ")
            withSquiggly(containsText, onCriteriaClick)
            if (state.selectedCriteria.isEmpty()) {
                AddExtraCriteria(onAddExtraCriteriaClick)
            }
            if (state.selectedCriteria.isNotEmpty()) {
                state.selectedCriteria.forEachIndexed { index, criteria ->
                    if (index != 0) {
                        append(" and")
                    }
                    Row {
                        append(" ")
                        withSquiggly("${criteria.describe()}") {
                            onExtraCriteriaClick.invoke(criteria)
                        }
                        if (index == state.selectedCriteria.lastIndex) {
                            AddExtraCriteria(onAddExtraCriteriaClick)
                        }
                    }
                    Spacer(Modifier.width(spacing))
                }
            }
            append(" then ")

            withSquiggly(state.action?.title ?: "do nothing", onActionClick)
        },
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 60.sp
        )
    )
}

@Composable
private fun AnnotatedString.Builder.AddExtraCriteria(onAddExtraCriteriaClick: () -> Unit) {
    append(" ")
    withLink(
        LinkAnnotation.Clickable(tag = "tag", linkInteractionListener = {
            onAddExtraCriteriaClick()
        })
    ) {
        appendInlineContent("plus", "plus")
    }
}

@Composable
fun RecentMatchingEmptySection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text(
            text = stringResource(R.string.rule_recent_matching_notifications),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
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
fun inlineAppIcon(icon: Drawable?): InlineTextContent {
    return InlineTextContent(
        placeholder = Placeholder(
            height = 22.sp, // roughly width of the word
            width = 22.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        Icon(
            painter = rememberDrawablePainter(icon),
            contentDescription = null,
            tint = Color.Unspecified, // show original icon color
            modifier = Modifier.size(22.dp).clip(CircleShape),
        )
    }
}

@Composable
fun inlinePlus(onClick: () -> Unit): InlineTextContent {
    return InlineTextContent(
        placeholder = Placeholder(
            height = 40.sp, // roughly width of the word
            width = 30.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onClick() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                "+",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExtraCriteriaBottomSheetPreview() {
    QuietTheme { ExtraCriteriaBottomSheetContent(items = getCriteriaTypes(listOf())) }
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
                selectedApps =
                listOf(
                    AppItem(
                        label = "Microsoft Teams",
                        packageName = "com.microsoft.teams",
                        icon =
                        AppCompatResources.getDrawable(
                            context, R.drawable.ic_launcher_foreground
                        )!!,
                    )
                ),
                criteriaText = listOf("meeting", "call"),
                selectedCriteria = listOf(TimeCriteria(listOf())),
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
                selectedApps =
                listOf(
                    AppItem(
                        label = "Microsoft Teams",
                        packageName = "com.microsoft.teams",
                        icon =
                        AppCompatResources.getDrawable(
                            context, R.drawable.ic_launcher_foreground
                        )!!,
                    )
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
