package id.rezyfr.quiet.screen.picktime

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.component.SecondaryButton
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun PickTimeScreen(
    args: PickTimeArgs,
    viewModel: PickTimeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var dialogData by remember { mutableStateOf<TimeRange?>(null) }

    LaunchedEffect(Unit) {
        viewModel.setTimeRanges(args.timeRanges)
    }

    PickTimeContent(
        state,
        onPickItemClick = {
            dialogData = it
        },
        onPickTimeConfirm = { viewModel.pickTime(args.type) },
        onResetClick = viewModel::reset,
        onApplyToAllClick = viewModel::applyToAll
    )

    if (dialogData != null) {
        TimeRangeDialog(
            timeRange = dialogData!!,
            onDismiss = { dialogData = null },
            onConfirm = { start, end ->
                viewModel.updateTimeRange(dialogData!!.day, start, end)
                dialogData = null
            }
        )
    }
}
@Composable
fun PickTimeContent(
    state: PickTimeViewModel.PickTimeState,
    modifier: Modifier = Modifier,
    onPickItemClick: (TimeRange) -> Unit = { },
    onPickTimeConfirm: () -> Unit = { },
    onResetClick: (DayOfWeek) -> Unit = {},
    onApplyToAllClick: (startMinutes: Int, endMinutes: Int) -> Unit = {_, _ -> }
) {
    val buttonText =
        if (state.isModified) {
            stringResource(R.string.timepick_selected_times)
        } else {
            stringResource(R.string.timepick_always_active)
        }
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(spacingX),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.timepick_helper),
                        style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.height(spacingX))
                    PrimaryButton(buttonText, Modifier.fillMaxWidth(), onClick = onPickTimeConfirm)
                }
            }
        }
    ) {
        Box(
            Modifier.fillMaxSize()
                .padding(it)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(spacingX),
                modifier = Modifier.padding(vertical = spacingXX, horizontal = spacingX)
            ) {
                items(state.days) { time ->
                    PickTimeItem(
                        modifier = Modifier.clickable(
                            onClick = {
                                onPickItemClick(time)
                            }
                        ),
                        day = time.day.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        onResetClick = {
                            onResetClick.invoke(time.day)
                        },
                        onApplyToAllClick = {
                            onApplyToAllClick.invoke(time.startMinutes, time.endMinutes)
                        },
                        startMinutes = time.startMinutes,
                        endMinutes = time.endMinutes,
                    )
                }
            }
        }
    }
}

@Composable
fun PickTimeItem(
    modifier: Modifier = Modifier,
    day: String = "Monday",
    startMinutes: Int = 0,
    endMinutes: Int = 1440,
    onApplyToAllClick : () -> Unit = {},
    onResetClick : () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val totalMinutes = 1440.toFloat()
    val startWeight: Float = startMinutes / totalMinutes
    val selectedWeight = (endMinutes - startMinutes).toFloat() / totalMinutes
    val endWeight: Float = 1f - (startWeight + selectedWeight)

    Column(modifier.background(color = MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,) {
            Text(
                text = day,
                style =
                MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                fontWeight = FontWeight.Bold
            )
            Box {

                SecondaryButton(
                    text = stringResource(R.string.options),
                    modifier = Modifier,
                    onClick = {
                        expanded = true
                    }
                )
                DropdownMenu(
                    modifier = Modifier.align(Alignment.TopEnd),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.apply_to_all)) },
                        onClick = {
                            onApplyToAllClick.invoke()
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.reset)) },
                        onClick = {
                            onResetClick.invoke()
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(spacingX))
        if (startMinutes == 0 && endMinutes == 0) {
            EmptyTimeRangeBar()
        } else {
            TimeRangeBar(startWeight, selectedWeight, endWeight)
        }
        Spacer(Modifier.height(spacing))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CompositionLocalProvider(
                LocalTextStyle provides
                    MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    )
            ) {
                Text("12AM")
                Text("6AM")
                Text("12PM")
                Text("6PM")
                Text("12AM")
            }
        }
    }
}

@Composable
private fun EmptyTimeRangeBar() {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
                .height(36.dp)
        )
    }
}

@Composable
private fun TimeRangeBar(
    startWeight: Float,
    selectedWeight: Float,
    endWeight: Float
) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
    ) {
        if (startWeight > 0) {
            Box(
                Modifier
                    .weight(startWeight)
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    .height(36.dp)
            )
        }
        Box(
            Modifier
                .weight(selectedWeight)
                .background(
                    color = MaterialTheme.colorScheme.primary
                )
                .height(36.dp)
        )
        if (endWeight > 0) {
            Box(
                Modifier
                    .weight(endWeight)
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    .height(36.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PickTimeScreenPreview() {
    QuietTheme {
        PickTimeContent(
            state =
            PickTimeViewModel.PickTimeState(
                days =
                listOf(
                    TimeRange(DayOfWeek.MONDAY, 60, 1200),
                    TimeRange(DayOfWeek.TUESDAY, 0, 0),
                    TimeRange(DayOfWeek.WEDNESDAY, 0, 0),
                    TimeRange(DayOfWeek.THURSDAY, 0, 0),
                    TimeRange(DayOfWeek.FRIDAY, 0, 0),
                    TimeRange(DayOfWeek.SATURDAY, 0, 0),
                    TimeRange(DayOfWeek.SUNDAY, 0, 0)
                )
            )
        )
    }
}
