package id.rezyfr.quiet.screen.pickapp

import android.R.attr.end
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.component.WavyText
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacingH
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXH
import id.rezyfr.quiet.ui.theme.spacingXX
import org.koin.androidx.compose.koinViewModel

@Composable
fun PickAppScreen(
    viewModel: PickAppViewModel = koinViewModel()
) {
    val pm = LocalContext.current.packageManager
    val state by viewModel.state.collectAsStateWithLifecycle()

    PickAppContent(
        onQueryChange = {
            viewModel.updateSearch(it)
        },
        allApps = state.filteredApps,
        selectedApp = state.selectedApp,
        onSelectApp = {
            viewModel.selectApp(it)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getInstalledApps(pm)
    }
}
@Composable
fun PickAppContent(
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    allApps: List<AppItem> = listOf(),
    isLoading: Boolean = false,
    selectedApp: AppItem? = null,
    onSelectApp: (AppItem) -> Unit = {},
    onPickAllApps: () -> Unit = {},
    onConfirmSelection: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(spacingXX)
    ) {
        // TITLE BLOCK
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(stringResource(R.string.pick_when_notification))
                Spacer(Modifier.width(spacingSmall))
                WavyText(text = stringResource(R.string.pick_is_from))
            }
        }

        Spacer(Modifier.height(spacingX))
        // SEARCH BAR
        SearchBar(
            onValueChange = onQueryChange
        )

        Spacer(Modifier.height(spacingXH))

        if (isLoading) {
            // ===== Loading State =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }

            Spacer(Modifier.height(spacingXH))

            PickButton(
                label = "Pick all apps",
                enabled = false,
                onClick = {}
            )

            return@Column
        }
        // ===== After Loading =====
        // SELECTED APP SECTION (optional)
        if (selectedApp != null) {
            Text(
                "Selected",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(10.dp))

            Box(
                Modifier.padding(end = spacingXX)
            ) {
                AppGridItem(
                    app = selectedApp,
                    selected = true,
                    onClick = {}
                )
            }

            Spacer(Modifier.height(spacingXH))

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Spacer(Modifier.height(spacingXH))
        }
        // APPS GRID
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            verticalArrangement = Arrangement.spacedBy(spacingXX),
            horizontalArrangement = Arrangement.spacedBy(spacingXX),
            modifier = Modifier.weight(1f)
        ) {
            items(allApps) { item ->
                AppGridItem(
                    app = item,
                    selected = selectedApp?.packageName == item.packageName,
                    onClick = { onSelectApp(item) }
                )
            }
        }

        Spacer(Modifier.height(spacingXH))
        // BOTTOM BUTTON
        if (selectedApp == null) {
            PickButton(
                label = "Pick all apps",
                onClick = onPickAllApps
            )
        } else {
            PickButton(
                label = "Pick ${selectedApp.label}",
                onClick = onConfirmSelection
            )
        }
    }
}
@Composable
fun AppGridItem(
    app: AppItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected)
        MaterialTheme.colorScheme.onBackground
    else
        MaterialTheme.colorScheme.surface
    val textColor = if (selected)
        MaterialTheme.colorScheme.surface
    else
        MaterialTheme.colorScheme.onSurface

    Surface(
        shape = RoundedCornerShape(spacingXX),
        color = background,
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .aspectRatio(1.2f)
            .clickable(onClick = onClick)
    ) {
        Column(
            Modifier.padding(spacingXX),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = rememberDrawablePainter(app.icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.height(spacingH))

            Text(
                app.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun PickButton(
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    PrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = spacingX),
        text = label,
        onClick = onClick,
        enabled = enabled
    )
}
@Composable
fun SearchBar(
    onValueChange: (String) -> Unit
) {
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
                tint = MaterialTheme.colorScheme.primary
            )
        },
        placeholder = { Text(stringResource(R.string.pick_search_placeholder)) },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    )
}

data class AppItem(
    val label: String,
    val icon: Drawable,
    val packageName: String
)
@Preview(showBackground = true)
@Composable
fun PreviewPickAppInitContent() {
    val context = LocalContext.current
    QuietTheme {
        PickAppContent(
            allApps = listOf(
                AppItem(
                    "App 1",
                    AppCompatResources.getDrawable(context, R.drawable.ic_launcher_foreground)!!,
                    "com.example.app1"
                ),
                AppItem(
                    "App 2",
                    AppCompatResources.getDrawable(context, R.drawable.ic_launcher_foreground)!!,
                    "com.example.app1"
                ),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPickAppSelectedContent() {
    val context = LocalContext.current
    val selected = AppItem(
        "App 1",
        AppCompatResources.getDrawable(context, R.drawable.ic_launcher_foreground)!!,
        "com.example.app1"
    )
    QuietTheme {
        PickAppContent(
            selectedApp = selected,
            allApps = listOf(
                selected,
                AppItem(
                    "App 2",
                    AppCompatResources.getDrawable(context, R.drawable.ic_launcher_foreground)!!,
                    "com.example.app1"
                ),
            )
        )
    }
}