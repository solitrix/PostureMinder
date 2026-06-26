package com.solitrix.postureminder.shared.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.solitrix.postureminder.shared.generated.resources.Res
import com.solitrix.postureminder.shared.generated.resources.*
import com.solitrix.postureminder.shared.ui.viewmodel.Action
import com.solitrix.postureminder.shared.ui.viewmodel.UiState
import com.solitrix.postureminder.shared.util.localizedName
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource

@Composable
fun StartScreen(
    state: UiState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    var now by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showSetPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            delay(1_000L)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${formatDate(now)}  ${formatTime(now)}",
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = formatCountdown(secondsToNextSlotBoundary(now)),
                style = MaterialTheme.typography.displayMedium.copy(fontFeatureSettings = "tnum"),
                modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
            )

            // Active schedule — simple dropdown switcher (no add/delete here)
            Box {
                val activeSetName = state.schedules.find { it.id == state.activeScheduleId }?.name ?: ""
                TextButton(onClick = { dropdownExpanded = true }) {
                    Text(activeSetName, style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                ) {
                    state.schedules.forEach { schedule ->
                        DropdownMenuItem(
                            text = { Text(schedule.name) },
                            onClick = {
                                onAction(Action.SetActiveSchedule(schedule.id))
                                dropdownExpanded = false
                            },
                        )
                    }
                }
            }

            PostureScreen(
                state = state,
                onAction = onAction,
                mode = PostureScreenMode.RUN,
                modifier = Modifier.weight(1f),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Button(
                    onClick = { onAction(Action.ToggleReminders) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.remindersRunning) Color(0xFFE53935) else Color(0xFF43A047),
                        contentColor = Color.White,
                    ),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (state.remindersRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.remindersRunning)
                                stringResource(Res.string.cd_pause_reminders)
                            else
                                stringResource(Res.string.cd_start_reminders),
                            modifier = Modifier.size(22.dp),
                        )
                        Icon(
                            imageVector = if (state.remindersRunning) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }

                Button(onClick = { showSetPicker = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(Res.string.cd_setup),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }

    // Setup button: manage schedules then navigate to edit mode
    if (showSetPicker) {
        SetManagerDialog(
            sets = state.schedules,
            activeSetId = state.activeScheduleId,
            onSelectSet = { onAction(Action.SetActiveSchedule(it)) },
            onAddSet = { onAction(Action.AddSchedule(it)) },
            onDeleteSet = { onAction(Action.DeleteSchedule(it.id)) },
            onDismiss = { showSetPicker = false },
            onDone = { showSetPicker = false; onAction(Action.NavigateToEdit) },
        )
    }
}

private fun secondsToNextSlotBoundary(dateTime: LocalDateTime): Int? {
    val now = dateTime.hour * 3600 + dateTime.minute * 60 + dateTime.second
    val windowStart = 8 * 3600
    val windowEnd = 18 * 3600
    val slotSecs = 30 * 60
    if (now >= windowEnd) return null
    val target = if (now < windowStart) windowStart
    else windowStart + ((now - windowStart) / slotSecs + 1) * slotSecs
    return if (target > windowEnd) null else target - now
}

private fun formatCountdown(seconds: Int?): String {
    if (seconds == null) return "--:--"
    return "${seconds / 60}:${pad(seconds % 60)}"
}

private fun formatTime(dateTime: LocalDateTime): String {
    val amPm = if (dateTime.hour < 12) "AM" else "PM"
    val hour12 = when (val h = dateTime.hour % 12) { 0 -> 12; else -> h }
    return "$hour12:${pad(dateTime.minute)} $amPm"
}

private fun formatDate(dateTime: LocalDateTime): String = dateTime.dayOfWeek.localizedName()

private fun pad(value: Int): String = value.toString().padStart(2, '0')
