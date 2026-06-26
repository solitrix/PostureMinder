package com.solitrix.postureminder.shared.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.solitrix.postureminder.shared.domain.model.ScheduleModel
import com.solitrix.postureminder.shared.generated.resources.Res
import com.solitrix.postureminder.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun SetManagerDialog(
    sets: List<ScheduleModel>,
    activeSetId: Long,
    onSelectSet: (Long) -> Unit,
    onAddSet: (String) -> Unit,
    onDeleteSet: (ScheduleModel) -> Unit,
    onDismiss: () -> Unit,
    onDone: (() -> Unit)? = null,
) {
    var newSetName by remember { mutableStateOf("") }
    var confirmDeleteTarget by remember { mutableStateOf<ScheduleModel?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.pick_schedule_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                sets.forEach { set ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSet(set.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = set.id == activeSetId,
                            onClick = { onSelectSet(set.id) }
                        )
                        Text(
                            text = set.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(
                            onClick = { confirmDeleteTarget = set },
                            enabled = sets.size > 1,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.cd_delete_set, set.name),
                                modifier = Modifier.size(18.dp),
                                tint = if (sets.size > 1)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = stringResource(Res.string.new_schedule_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newSetName,
                        onValueChange = { newSetName = it },
                        placeholder = { Text(stringResource(Res.string.schedule_name_placeholder)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            if (newSetName.isNotBlank()) {
                                onAddSet(newSetName.trim())
                                newSetName = ""
                            }
                        },
                        enabled = newSetName.isNotBlank()
                    ) {
                        Text(stringResource(Res.string.btn_add))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDone?.invoke() ?: onDismiss() }) { Text(stringResource(Res.string.btn_edit)) }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text(stringResource(Res.string.btn_cancel)) }
        },
    )

    confirmDeleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { confirmDeleteTarget = null },
            title = { Text(stringResource(Res.string.delete_schedule_title, target.name)) },
            text = {
                Text(stringResource(Res.string.delete_schedule_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteSet(target)
                        confirmDeleteTarget = null
                    }
                ) {
                    Text(stringResource(Res.string.btn_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteTarget = null }) { Text(stringResource(Res.string.btn_cancel)) }
            }
        )
    }
}
