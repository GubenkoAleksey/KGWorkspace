@file:OptIn(ExperimentalMaterial3Api::class)

package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hubenko.feature.admin.ui.model.EmployeeStatusUi
import com.hubenko.feature.admin.ui.model.StatusTypeUi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun EditStatusDialog(
    status: EmployeeStatusUi,
    availableStatusTypes: List<StatusTypeUi>,
    onSave: (statusType: String, startTime: Long, endTime: Long?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStatusType by remember { mutableStateOf(status.status) }
    var startMs by remember { mutableLongStateOf(status.startTime) }
    var endMs by remember { mutableStateOf(status.endTime) }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val sdf = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редагувати статус") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = availableStatusTypes.find { it.type == selectedStatusType }?.label ?: selectedStatusType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Тип статусу") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        availableStatusTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.label) },
                                onClick = {
                                    selectedStatusType = type.type
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                DateTimeRow(
                    label = "Початок",
                    timeMs = startMs,
                    sdf = sdf,
                    onDateClick = { showStartDatePicker = true },
                    onTimeClick = { showStartTimePicker = true },
                    onClear = null
                )

                DateTimeRow(
                    label = "Кінець",
                    timeMs = endMs,
                    sdf = sdf,
                    onDateClick = { showEndDatePicker = true },
                    onTimeClick = { showEndTimePicker = true },
                    onClear = { endMs = null }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selectedStatusType, startMs, endMs) }) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )

    if (showStartDatePicker) {
        val cal = calendarFrom(startMs)
        val state = rememberDatePickerState(initialSelectedDateMillis = utcMidnight(cal))
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { startMs = applyDate(it, startMs) }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Скасувати") }
            }
        ) { DatePicker(state = state) }
    }

    if (showStartTimePicker) {
        val cal = calendarFrom(startMs)
        val state = rememberTimePickerState(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            is24Hour = true
        )
        TimePickerDialog(
            onConfirm = {
                startMs = applyTime(startMs, state.hour, state.minute)
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        ) { TimePicker(state = state) }
    }

    if (showEndDatePicker) {
        val currentEnd = endMs ?: System.currentTimeMillis()
        val cal = calendarFrom(currentEnd)
        val state = rememberDatePickerState(initialSelectedDateMillis = utcMidnight(cal))
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { endMs = applyDate(it, endMs ?: startMs) }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Скасувати") }
            }
        ) { DatePicker(state = state) }
    }

    if (showEndTimePicker) {
        val currentEnd = endMs ?: startMs
        val cal = calendarFrom(currentEnd)
        val state = rememberTimePickerState(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            is24Hour = true
        )
        TimePickerDialog(
            onConfirm = {
                endMs = applyTime(endMs ?: startMs, state.hour, state.minute)
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        ) { TimePicker(state = state) }
    }
}

@Composable
private fun DateTimeRow(
    label: String,
    timeMs: Long?,
    sdf: SimpleDateFormat,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onClear: (() -> Unit)?
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val text = timeMs?.let { sdf.format(it) } ?: "—"
            TextButton(onClick = onDateClick) {
                Text(text.take(10))
            }
            TextButton(onClick = onTimeClick) {
                Text(if (timeMs != null) text.takeLast(5) else "—")
            }
            if (onClear != null) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Очистити кінець"
                    )
                }
            }
        }
    }
}

@Composable
private fun TimePickerDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Виберіть час") },
        text = { content() },
        confirmButton = { TextButton(onClick = onConfirm) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Скасувати") } }
    )
}

private fun calendarFrom(ms: Long): Calendar =
    Calendar.getInstance().apply { timeInMillis = ms }

private fun utcMidnight(cal: Calendar): Long =
    Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun applyDate(utcMidnightMs: Long, existingMs: Long): Long {
    val dateCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMidnightMs }
    val timeCal = calendarFrom(existingMs)
    return Calendar.getInstance().apply {
        set(dateCal.get(Calendar.YEAR), dateCal.get(Calendar.MONTH), dateCal.get(Calendar.DAY_OF_MONTH),
            timeCal.get(Calendar.HOUR_OF_DAY), timeCal.get(Calendar.MINUTE), 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun applyTime(existingMs: Long, hour: Int, minute: Int): Long =
    calendarFrom(existingMs).apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
