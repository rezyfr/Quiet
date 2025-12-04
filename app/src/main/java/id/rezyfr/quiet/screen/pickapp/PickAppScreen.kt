package id.rezyfr.quiet.screen.pickapp

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.ui.component.ExtendedSpansText
import id.rezyfr.quiet.ui.component.withSquiggly
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacingH
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXH
import id.rezyfr.quiet.ui.theme.spacingXX
import org.koin.androidx.compose.koinViewModel

@Composable
fun PickAppScreen(
    pickedApps: List<String> = listOf(),
    viewModel: PickAppViewModel = koinViewModel()
) {
    val pm = LocalContext.current.packageManager
    val state by viewModel.state.collectAsStateWithLifecycle()

    PickAppContent(
        onQueryChange = { viewModel.updateSearch(it) },
        allApps = state.filteredApps,
        selectedApp = state.selectedApp,
        isLoading = state.isLoading,
        onSelectApp = { app, isSelected ->
            viewModel.selectApp(app, isSelected)
        },
        onConfirmSelection = { viewModel.pickApp() },
    )

    LaunchedEffect(Unit) {
        viewModel.setPickedApps(pickedApps, pm)
        viewModel.getInstalledApps(pm)
    }
}

@Composable
fun PickAppContent(
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    allApps: List<AppItem> = listOf(),
    isLoading: Boolean = false,
    selectedApp: List<AppItem> = listOf(),
    onSelectApp: (AppItem, Boolean) -> Unit = { _, _ -> },
    onPickAllApps: () -> Unit = {},
    onConfirmSelection: () -> Unit = {},
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(spacingXX)
    ) {
        // TITLE BLOCK
        CompositionLocalProvider(
            LocalTextStyle provides
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
        ) {
            ExtendedSpansText(
                text = buildAnnotatedString {
                    append(stringResource(R.string.when_notification))
                    append(" ")
                    withSquiggly(stringResource(R.string.pick_is_from))
                }
            )
        }

        Spacer(Modifier.height(spacingX))
        // SEARCH BAR
        SearchBar(onValueChange = onQueryChange)

        Spacer(Modifier.height(spacingXH))

        if (isLoading) {
            LoadingContent(Modifier.weight(1f).fillMaxWidth())
        } else {
            // 🔥 This Box gives FINITE height to the scroll container
            Box(
                modifier = Modifier
                    .weight(1f) // <-- allows scrolling only in this section
                    .fillMaxWidth()
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(140.dp),
                    verticalArrangement = Arrangement.spacedBy(spacingX),
                    horizontalArrangement = Arrangement.spacedBy(spacingX)
                ) {
                    // SELECTED APPS

                    if (selectedApp.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                "Selected",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Spacer(Modifier.height(spacingX))
                        }

                        items(selectedApp, { it.packageName.plus(".selected") }) { app ->
                            val isSelected = selectedApp.any { it.packageName == app.packageName }
                            AppGridItem(
                                app,
                                selected = isSelected,
                                onClick = {
                                    onSelectApp(app, isSelected)
                                }
                            )
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Spacer(Modifier.height(spacingXH))
                            HorizontalDivider()
                        }
                    }

                    items(allApps, {it.packageName } ) { app ->
                        val isSelected = selectedApp.any { it.packageName == app.packageName }
                        AppGridItem(
                            app = app,
                            selected = isSelected,
                            onClick = { onSelectApp(app, isSelected) }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(spacingXH))

        if (selectedApp.isEmpty()) {
            PickButton(label = "Pick all apps", onClick = onPickAllApps)
        } else {
            PickButton(
                label = "Pick ${
                    if (selectedApp.size > 1) "these apps" else selectedApp.first().label
                }",
                onClick = onConfirmSelection
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 4.dp)
    }

    Spacer(Modifier.height(spacingXH))

    PickButton(label = "Pick all apps", enabled = false, onClick = {})
}

@Composable
fun AppGridItem(
    app: AppItem,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 500)
    )

    val textColor =
        if (selected) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.onSurface
        }

    val animatedSize by animateSizeAsState(
        targetValue = if (selected) Size(36f, 36f) else Size(24f, 24f),
        animationSpec = tween(durationMillis = 300)
    )

    Surface(
        shape = RoundedCornerShape(spacingXX),
        color = animatedBackgroundColor,
        modifier = modifier
            .fillMaxWidth(0.5f)
            .aspectRatio(1.5f)
            .clickable(onClick = {
                onClick.invoke()
            }),
    ) {
        Column(
            Modifier.padding(spacingXX),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Icon(
                painter = rememberDrawablePainter(app.icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(
                    animatedSize.height.dp, animatedSize.width.dp
                ),
            )

            Spacer(Modifier.height(spacingH))

            Text(
                app.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun PickButton(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    PrimaryButton(
        modifier = modifier.fillMaxWidth().padding(bottom = spacingX),
        text = label,
        onClick = onClick,
        enabled = enabled,
    )
}

@Composable
fun SearchBar(modifier: Modifier = Modifier, onValueChange: (String) -> Unit = {}) {
    val query = remember { mutableStateOf("") }
    TextField(
        value = query.value,
        onValueChange = {
            query.value = it
            onValueChange.invoke(it)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        placeholder = { Text(stringResource(R.string.pick_search_placeholder)) },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors =
        TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = modifier.fillMaxWidth().height(56.dp),
    )
}

data class AppItem(val label: String, val icon: Drawable?, val packageName: String)

@Preview(showBackground = true)
@Composable
private fun PreviewPickAppInitContent() {
    val context = LocalContext.current
    QuietTheme {
        PickAppContent(
            allApps =
            listOf(
                AppItem(
                    "App 1",
                    AppCompatResources.getDrawable(
                        context, R.drawable.ic_launcher_foreground
                    )!!,
                    "com.example.app1",
                ),
                AppItem(
                    "App 2",
                    AppCompatResources.getDrawable(
                        context, R.drawable.ic_launcher_foreground
                    )!!,
                    "com.example.app1",
                ),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPickAppSelectedContent() {
    val context = LocalContext.current
    val selected =
        AppItem(
            "App 1",
            AppCompatResources.getDrawable(context, R.drawable.ic_launcher_foreground)!!,
            "com.example.app1",
        )
    QuietTheme {
        PickAppContent(
            selectedApp = listOf(selected),
            allApps =
            listOf(
                selected,
                AppItem(
                    "App 2",
                    AppCompatResources.getDrawable(
                        context, R.drawable.ic_launcher_foreground
                    )!!,
                    "com.example.app1",
                ),
            ),
        )
    }
}
