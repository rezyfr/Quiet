package id.rezyfr.quiet.screen.welcome

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.rezyfr.quiet.App
import id.rezyfr.quiet.R
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXH
import id.rezyfr.quiet.ui.theme.spacingXX
import id.rezyfr.quiet.ui.theme.spacingXXX
import id.rezyfr.quiet.util.isIgnoringBatteryOptimizations
import id.rezyfr.quiet.util.isNotificationAccessGranted
import id.rezyfr.quiet.util.isNotificationAllowed
import id.rezyfr.quiet.util.requestDisableBatteryOptimization
import org.koin.androidx.compose.koinViewModel

@SuppressLint("BatteryLife")
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // Also re-check whenever the screen resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && isIgnoringBatteryOptimizations(context)) {
                if (isNotificationAccessGranted(context)) {
                    viewModel.enableQuiet()
                }

                if (isIgnoringBatteryOptimizations(context)) {
                    viewModel.checkBackground()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose  {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val batterySettingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // when user returns from settings, re-check

            if (isIgnoringBatteryOptimizations(context)) {
                viewModel.checkBackground()
            }
        }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // After the user responds, check the notification status again
            // The ViewModel will update the state based on the new permission status
            viewModel.checkNotification()
        }
    )

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacingXX)
        ) {
            Text(
                stringResource(R.string.welcome_header),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(spacingX))
            Text(
                stringResource(R.string.welcome_body),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(spacingXXX))
            RequiredAccessCard(
                index = 1,
                title = R.string.welcome_allow_notifications_title,
                body = R.string.welcome_allow_notifications_desc,
                allowed = state.notificationAllowed || isNotificationAllowed(context)
            ) {
                // request notification permission
                if (!state.notificationAllowed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    viewModel.checkNotification()
                }
            }
            Spacer(Modifier.height(spacingXX))
            RequiredAccessCard(
                index = 2,
                title = R.string.welcome_allow_background_title,
                body = R.string.welcome_allow_background_desc,
                allowed = state.backgroundAllowed || isIgnoringBatteryOptimizations(context)
            ) {
                requestDisableBatteryOptimization(
                    context = context,
                    fallbackLauncher = batterySettingsLauncher
                )
            }
            Spacer(Modifier.height(spacingXX))
            RequiredAccessCard(
                index = 3,
                title = R.string.welcome_enable_title,
                body = R.string.welcome_enable_desc,
                allowed = state.enabled || isNotificationAccessGranted(context)
            ) {
                if (!isNotificationAccessGranted(context)) {
                    val intent = Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }
            }
            Spacer(Modifier.height(spacingX))
        }
    }
}

@Composable
fun RequiredAccessCard(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes body: Int,
    index: Int = 1,
    allowed: Boolean = false,
    onClick: () -> Unit = { }
) {
    val contentColor = if (allowed) MaterialTheme.colorScheme.onSecondaryContainer
    else MaterialTheme.colorScheme.onPrimaryContainer
    val containerColor = if (allowed) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.primaryContainer
    val badgeText = if (allowed) "✓" else index.toString()
    Card(
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .clickable(true, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(spacingXH),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                badgeText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        Color.Transparent, CircleShape
                    )
                    .border(
                        BorderStroke(
                            1.5.dp,
                            LocalContentColor.current
                        ),
                        CircleShape
                    )
                    .wrapContentSize(align = Alignment.Center)
            )
            Spacer(Modifier.width(spacingX))
            Column {
                Text(stringResource(title), color = contentColor, fontWeight = FontWeight.SemiBold)
                AnimatedVisibility(!allowed) {
                    Text(stringResource(body), color = contentColor)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()
}