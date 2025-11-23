package id.rezyfr.quiet.screen.rule

import android.content.pm.PackageManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import id.rezyfr.quiet.screen.pickapp.AppItem
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacingX
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddRuleScreen(
    navController: NavController,
    onContainsClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    viewModel: AddRuleScreenViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val pm = context.packageManager

    val backStackEntry = navController.currentBackStackEntryAsState().value

    val appPackageName = backStackEntry
        ?.savedStateHandle
        ?.get<String>("key_pick_apps")

    val pickedApp = remember(appPackageName) {
        try {
            appPackageName?.let {
                val info = pm.getApplicationInfo(it, 0)
                AppItem(
                    label = info.loadLabel(pm).toString(),
                    icon = info.loadIcon(pm),
                    packageName = it
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            print(e)
            null
        }
    }

    LaunchedEffect(appPackageName) {
        if (appPackageName != null) {
            backStackEntry.savedStateHandle.remove<String>("key_pick_apps")
            viewModel.setAppItem(pickedApp)
        }
    }

    val criteria = backStackEntry?.savedStateHandle?.get<List<String>>("key_criteria")

    LaunchedEffect(criteria) {
        if (criteria != null) {
            backStackEntry.savedStateHandle.remove<List<String>>("key_criteria")
            viewModel.setCriteria(criteria)
        }
    }

    val state by viewModel.state.collectAsState()

    AddRuleContent(
        onAppClick = {
            viewModel.navigateToPickApp()
        },
        onCriteriaClick = {
            viewModel.navigateToPickCriteria()
        },
        state = state,
    )
}

@Composable
fun AddRuleContent(
    state: AddRuleScreenViewModel.AddRuleScreenState,
    modifier: Modifier = Modifier,
    onAppClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCriteriaClick: () -> Unit = {},
) {
    Surface(
        modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacingX)
        ) {
            RuleEditorHeader(
                state = state,
                onAppClick = onAppClick,
                onCriteriaClick = onCriteriaClick
            )

            Spacer(Modifier.height(12.dp))

            PrimaryButton(
                text = stringResource(R.string.rule_save),
                modifier = Modifier.fillMaxWidth(),
                onClick = onSaveClick
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            RecentMatchingNotificationsSection()
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
    onActionClick: () -> Unit = {},
) {
    val appText = state.appItem?.label ?: stringResource(R.string.rule_any_app)
    val containsText = if (state.criteriaText.isEmpty()) {
        stringResource(R.string.rule_contains_anything)
    } else {
        "contains ${state.criteriaText.fastJoinToString(" or ") {
            "\"${it.capitalize()}\""
        }}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Shared style like BuzzKill
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        ) {
            // Line 1: When I get a notification
            Text(stringResource(R.string.rule_when_notification))

            Spacer(Modifier.height(24.dp))

            // Line 2: from [icon] [appLabel] that
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(stringResource(R.string.from))

                if (state.appItem?.icon != null) {
                    Icon(
                        painter = rememberDrawablePainter(state.appItem.icon),
                        contentDescription = null,
                        tint = Color.Unspecified, // show original icon color
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                }

                WavyText(
                    text = appText,
                    onClick = onAppClick
                )

                Text(" that")
            }
            Spacer(Modifier.height(8.dp))
            // Line 3: contains "Hit Target" + [+] button
            Row(
                verticalAlignment = Alignment.Top
            ) {
                WavyText(
                    text = containsText,
                    onClick = onCriteriaClick
                )

                /*Spacer(Modifier.width(8.dp))

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onAddExtraCriteriaClick),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }*/
            }

            Spacer(Modifier.height(8.dp))
            // Line 4: then [icon] [actionLabel]
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(stringResource(R.string.then))

                if (state.actionIcon != null) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = state.actionIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.background,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                }

                WavyText(
                    text = state.actionLabel,
                    onClick = onActionClick
                )
            }
        }
    }
}

@Composable
fun RecentMatchingNotificationsSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.rule_recent_matching_notifications),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.rule_no_recent_notifications_match),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddRuleEmptyPreview() {
    QuietTheme {
        AddRuleContent(
            state = AddRuleScreenViewModel.AddRuleScreenState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddRuleFilledPreview() {
    val context = LocalContext.current
    QuietTheme {
        AddRuleContent(
            state = AddRuleScreenViewModel.AddRuleScreenState(
                appItem = AppItem(
                    label = "Microsoft Teams",
                    packageName = "com.microsoft.teams",
                    icon = AppCompatResources.getDrawable(context, R.drawable.ic_launcher_foreground)!!
                ),
                criteriaText = listOf("meeting", "call"),
                actionLabel = "mute"
            ),
        )
    }
}