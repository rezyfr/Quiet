package id.rezyfr.quiet.screen.picktime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingH
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangeDialog(
    timeRange: TimeRange,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedStart by remember { mutableStateOf(timeRange.startMinutes) }
    var selectedEnd by remember { mutableStateOf(timeRange.endMinutes) }
    var pickingStart by remember { mutableStateOf(false) }
    var pickingEnd by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "When rule is active",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(spacingXX))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeInputField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = spacing),
                        label = formatMinutes(selectedStart),
                        onClick = { pickingStart = true }
                    )

                    Text(
                        "—",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TimeInputField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = spacing),
                        label = formatMinutes(selectedEnd),
                        onClick = { pickingEnd = true }
                    )
                }
            }
        },
        confirmButton = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.done),
                onClick = { onConfirm(selectedStart, selectedEnd) }
            )
        },
        dismissButton = {
            TextButton(modifier = Modifier.fillMaxWidth(), onClick = onDismiss) {
                Text(
                    stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
    // TimePicker sheet for Start
    if (pickingStart) {
        TimePickerSheet(
            initial = selectedStart,
            onDismiss = { pickingStart = false },
            onSelected = {
                selectedStart = it
                pickingStart = false
            }
        )
    }
    // TimePicker sheet for End
    if (pickingEnd) {
        TimePickerSheet(
            initial = selectedEnd,
            onDismiss = { pickingEnd = false },
            onSelected = {
                selectedEnd = it
                pickingEnd = false
            }
        )
    }
}

@Composable
fun TimeInputField(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
        color = Color.Transparent,
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = spacingSmall)
    ) {
        Box(
            modifier = Modifier.padding(
                vertical = spacingH,
                horizontal = spacingX
            )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSheet(
    initial: Int,
    onDismiss: () -> Unit,
    onSelected: (Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = minOf(initial / 60, 23),
        initialMinute = initial % 60,
        is24Hour = false
    )

    TimePickerDialog(
        title = { TimePickerDialogDefaults.Title(displayMode = TimePickerDisplayMode.Input) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSelected(state.hour * 60 + state.minute)
                }
            ) {
                Text(stringResource(R.string.done))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    ) {
        TimeInput(state = state)
    }
}

fun formatMinutes(minuteOfDay: Int): String {
    val hour24 = minuteOfDay / 60
    val minute = minuteOfDay % 60

    val hour12 = when {
        hour24 == 0 -> 12           // 0:00 → 12 AM
        hour24 > 12 -> hour24 - 12  // 13:00 → 1 PM
        else -> hour24              // 1–12 → 1–12
    }

    val amPm = if (hour24 < 12) "AM" else "PM"

    return String.format("%d:%02d %s", hour12, minute, amPm)
}

@Preview
@Composable
fun TimePickerDialogPreview() {
    QuietTheme {
        TimeRangeDialog(
            TimeRange(DayOfWeek.SATURDAY, 120, 180),{}, { _, _ -> }
        )
    }
}
