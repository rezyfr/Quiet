package id.rezyfr.quiet.screen.criteria

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.ui.component.ExtendedSpansText
import id.rezyfr.quiet.ui.component.withSquiggly
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingH
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX
import id.rezyfr.quiet.ui.theme.spacingXXX
import id.rezyfr.quiet.util.drawable
import org.koin.androidx.compose.koinViewModel

@Composable
fun CriteriaScreen(viewModel: CriteriaViewModel = koinViewModel()) {
    var showPhraseDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (showPhraseDialog) {
        PhraseDialog(
            onDismiss = { showPhraseDialog = false },
            onDone = { newPhrase ->
                viewModel.addPhrase(newPhrase)
                showPhraseDialog = false
            },
        )
    }

    CriteriaContent(
        onPhraseClick = { showPhraseDialog = true },
        onRemovePhrase = { viewModel.removePhrase(it) },
        onApplyClick = { viewModel.navigateToAddRules() },
        state = state,
    )
}

@Composable
fun CriteriaContent(
    state: CriteriaViewModel.State,
    modifier: Modifier = Modifier,
    onPhraseClick: () -> Unit = {},
    onRemovePhrase: (String) -> Unit = {},
    onApplyClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = { CriteriaTopBar() },
        bottomBar = { CriteriaBottomBar(onApplyClick) },
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(horizontal = spacingX),
            ) {
                PhraseListSection(
                    phrases = state.phrase, onRemove = { phrase -> onRemovePhrase(phrase) }
                )
                Spacer(Modifier.height(spacingXXX))
                CriteriaGridItem(
                    modifier = Modifier.fillMaxWidth(0.3f),
                    criteria =
                    CriteriaItem(
                        name = stringResource(R.string.criteria_phrase_title),
                        icon = R.drawable.ic_double_quotes,
                        desc = stringResource(R.string.criteria_phrase_desc),
                    ),
                    isCriteriaEmpty = state.phrase.isEmpty(),
                    onClick = onPhraseClick,
                )
            }
        }
    }
}

@Composable
private fun CriteriaBottomBar(onApplyClick: () -> Unit) {
    Column(Modifier.background(MaterialTheme.colorScheme.surface).padding(spacingXX)) {
        Text(
            text = stringResource(R.string.criteria_apply_filter_info),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(spacingX))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.apply_filter),
            onClick = onApplyClick,
        )
    }
}

@Composable
private fun CriteriaTopBar() {
    CompositionLocalProvider(
        LocalTextStyle provides
            MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
    ) {
        Column(Modifier.padding(spacingXX)) {
            ExtendedSpansText(
                text = buildAnnotatedString {
                    append(stringResource(R.string.when_notification))
                    append(" ")
                    withSquiggly(stringResource(R.string.criteria_contains_any_of))
                }
            )
        }
    }
}

@Composable
fun CriteriaGridItem(
    criteria: CriteriaItem,
    modifier: Modifier = Modifier,
    isCriteriaEmpty: Boolean = false,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(isCriteriaEmpty) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    criteria.desc,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center,
                )
                Icon(
                    R.drawable.ic_arrow_down.drawable(context),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                )
                Spacer(Modifier.height(spacingXX))
            }
        }
        Surface(
            shape = RoundedCornerShape(spacingH),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        ) {
            Column(
                Modifier.padding(vertical = spacingX, horizontal = spacingH),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    R.drawable.ic_double_quotes.drawable(context),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.height(spacingSmall))
                Text(
                    criteria.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun PhraseDialog(onDismiss: () -> Unit, onDone: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        PhraseDialogContent(onDismiss = onDismiss, onDone = onDone)
    }
}

@Composable
fun PhraseDialogContent(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onDone: (String) -> Unit = {},
) {
    var text by remember { mutableStateOf("") }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.padding(spacingXX).fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(spacingXX)) {
            // Title
            ExtendedSpansText(
                text = buildAnnotatedString {
                    withSquiggly(stringResource(R.string.criteria_notification_contains))
                }
            )
            Spacer(Modifier.height(spacingX))
            // Input
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text(stringResource(R.string.start_typing), fontWeight = FontWeight.SemiBold)
                },
                shape = RoundedCornerShape(16.dp),
                colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(spacingXX))
            // Done button
            PrimaryButton(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                text = stringResource(R.string.done),
                onClick = { onDone(text) },
            )

            Spacer(Modifier.height(spacingXX))
            // Cancel
            Text(
                text = stringResource(R.string.cancel),
                modifier =
                Modifier.align(Alignment.CenterHorizontally).clickable(onClick = onDismiss),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
fun PhraseListSection(
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
    phrases: List<String> = emptyList(),
) {
    if (phrases.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        phrases.forEachIndexed { index, phrase ->
            PhraseCard(text = phrase, showOr = index != 0, onRemove = { onRemove(phrase) })
            Spacer(Modifier.height(spacing))
        }
    }
}

@Composable
fun PhraseCard(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    showOr: Boolean = false,
) {
    Box(modifier) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.padding(spacing).fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(spacingX).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = text,
                    style =
                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp).clickable(onClick = onRemove),
                )
            }
        }

        if (showOr) {
            Box(
                Modifier.align(Alignment.TopCenter)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    "or",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = spacing),
                )
            }
        }
    }
}

data class CriteriaItem(val name: String, @DrawableRes val icon: Int, val desc: String)

@Composable
@Preview(showBackground = true)
private fun PreviewCriteriaEmptyContent() {
    QuietTheme { CriteriaContent(state = CriteriaViewModel.State()) }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCriteriaFilledContent() {
    QuietTheme {
        CriteriaContent(
            state = CriteriaViewModel.State(listOf("Example", "Example 2", "Example 3"))
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPhraseDialog() {
    QuietTheme {
        Box(
            modifier =
            Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            PhraseDialogContent()
        }
    }
}
