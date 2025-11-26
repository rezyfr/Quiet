package id.rezyfr.quiet.screen.rule

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import id.rezyfr.quiet.domain.NotificationModel
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

    LaunchedEffect(Unit) { viewModel.getRecentNotification(pm, null) }

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
        state = state,
    )
}

@Composable
fun AddRuleContent(
    state: AddRuleScreenViewModel.AddRuleScreenState,
    modifier: Modifier = Modifier,
    onAppClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    onCriteriaClick: () -> Unit = {},
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
fun RuleEditorHeader(
    state: AddRuleScreenViewModel.AddRuleScreenState,
    modifier: Modifier = Modifier,
    onAppClick: () -> Unit = {},
    onCriteriaClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
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

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
        // Shared style like BuzzKill
        CompositionLocalProvider(
            LocalTextStyle provides
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )) {
                // Line 1: When I get a notification
                Text(stringResource(R.string.rule_when_notification))

                Spacer(Modifier.height(24.dp))
                // Line 2: from [icon] [appLabel] that
                RuleApps(state, state.appItem?.icon, appText, onAppClick)
                Spacer(Modifier.height(8.dp))

                RuleCriteria(containsText, onCriteriaClick)

                Spacer(Modifier.height(8.dp))
                // Line 4: then [icon] [actionLabel]
                RuleActions(state, onActionClick)
            }
    }
}

@Composable
private fun RuleCriteria(containsText: String, onCriteriaClick: () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        WavyText(text = containsText, onClick = onCriteriaClick)
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
    notifications: List<Pair<NotificationModel, AppItem>>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(1.5.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))

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
fun RecentNotificationCard(item: Pair<NotificationModel, AppItem>, modifier: Modifier = Modifier) {
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
                                fontWeight = FontWeight.SemiBold),
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

@Preview(showBackground = true)
@Composable
private fun AddRuleEmptyPreview() {
    QuietTheme { AddRuleContent(state = AddRuleScreenViewModel.AddRuleScreenState()) }
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
                                    context, R.drawable.ic_launcher_foreground)!!,
                        ),
                    criteriaText = listOf("meeting", "call"),
                    action = null,
                ))
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
                                    context, R.drawable.ic_launcher_foreground)!!,
                        ),
                    criteriaText = listOf("meeting", "call"),
                    action = null,
                    notificationList =
                        listOf(
                            Pair(
                                NotificationModel(
                                    sbnKey = "sbnKey",
                                    packageName = "com.btpn.dc",
                                    title = "Streaming Makin Hemat",
                                    text =
                                        "Karena ada cashback 50% untuk bayar layanan steaming favorit pakai Kartu Kredit Jenius. Khusus buat kamu, Cek di sini\uD83D\uDC47\uD83C\uDFFD\n",
                                    postTime = 1764126566104,
                                    saved = true,
                                ),
                                AppItem(
                                    label = "Jenius",
                                    packageName = "com.btpn.dc",
                                    icon =
                                        AppCompatResources.getDrawable(
                                            context, R.drawable.ic_launcher_foreground)!!,
                                ),
                            )),
                ))
    }
}
