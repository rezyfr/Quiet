package id.rezyfr.quiet.screen.main.rules

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.rezyfr.quiet.R
import id.rezyfr.quiet.domain.Rule
import id.rezyfr.quiet.screen.action.ActionItem
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXH
import id.rezyfr.quiet.ui.theme.spacingXX
import id.rezyfr.quiet.ui.theme.spacingXXX
import id.rezyfr.quiet.ui.theme.spacingXXXXX
import org.koin.androidx.compose.koinViewModel

@Composable
fun RulesScreen(modifier: Modifier = Modifier, viewModel: RulesScreenViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    RulesContent(
        modifier,
        state = state,
        onCreateRuleClick = viewModel::navigateToAddRules
    )

    LaunchedEffect(Unit) {
        viewModel.getRules()
    }
}

@Composable
fun RulesContent(
    modifier: Modifier = Modifier,
    onCreateRuleClick: () -> Unit = {},
    state: RulesScreenViewModel.RulesScreenState
) {
    if (state.rules is ViewState.Empty) {
        RulesEmptyContent(modifier, onCreateRuleClick)
    } else if (state.rules is ViewState.Success) {
        RulesMainContent(modifier, onCreateRuleClick = onCreateRuleClick, rules = state.rules.data!!)
    }
}

@Composable
fun RulesMainContent(
    modifier: Modifier = Modifier,
    rules: List<Rule>,
    onCreateRuleClick: () -> Unit = {},
    onRuleToggleClick: (Rule) -> Unit = {},
    onRuleClick: (Rule) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            CreateRuleFab(onClick = onCreateRuleClick)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(spacing)
                .fillMaxSize()
        ) {
            RulesMainHeader()

            Spacer(Modifier.height(spacingXX))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = spacingX,
                    end = spacingX,
                    bottom = spacingXXXXX
                ),
                verticalArrangement = Arrangement.spacedBy(spacingX)
            ) {
                items(rules) { rule ->
                    RuleItemContent(
                        rule = rule,
                        onToggle = { onRuleToggleClick(rule) },
                        onClick = { onRuleClick(rule) },
                        onMenuClick = { }
                    )
                }
            }
        }
    }
}

@Composable
fun RulesMainHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spacingXXX, start = spacingXX, end = spacingXX)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications, // replace with your icon
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(52.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(spacingX))

        Text(
            text = "Notification rules",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(spacing))

        Text(
            text = "When you get a notification, if it matches any of the following rules it will perform the chosen action.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RuleItemContent(
    rule: Rule,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(spacingX)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Enabled",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.width(spacingSmall))
                    Switch(
                        checked = rule.enabled,
                        onCheckedChange = onToggle
                    )
                }
            }

            Spacer(Modifier.height(spacingX))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = rule.text,  // e.g. "When I get a notification from Gmail during schedule then mute"
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(spacingX)
                )
            }
        }
    }
}

@Composable
fun CreateRuleFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacingX, vertical = spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(spacing))
            Text(
                text = "Create rule",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RulesEmptyContent(modifier: Modifier, onCreateRuleClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacingXX)
        ) {
            // Icon bubble
            Box(
                modifier =
                Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(16.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications, // replace with your icon
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(Modifier.height(spacingXH))

            Text(
                text = stringResource(R.string.rule_add_first),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(spacing))

            Text(
                text =
                stringResource(R.string.rule_add_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(spacingXX))

            RulesFooter(onCreateRuleClick = onCreateRuleClick)

            Spacer(Modifier.height(spacingX))
        }
    }
}

@Composable
private fun RulesFooter(onCreateRuleClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Button(
            onClick = onCreateRuleClick,
            shape = RoundedCornerShape(24.dp),
            colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(spacing))
            Text("Create rule", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
@Preview
fun PreviewRulesMainContent() {
    QuietTheme {
        RulesMainContent(
            rules = listOf(
                Rule(
                    listOf("id.quiet.rezyfr"),
                        keywords = listOf("Sampah"),
                        dayRange = null,
                        text = "When I get a notification from \"Teams\" during schedule then mute",
                        action = ActionItem(
                            "mute","Mute","mute",-1
                        ),
                        enabled = false,
                )
            )
        )
    }
}