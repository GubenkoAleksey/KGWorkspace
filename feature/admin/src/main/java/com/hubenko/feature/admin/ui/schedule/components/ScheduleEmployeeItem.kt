package com.hubenko.feature.admin.ui.schedule.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText
import com.hubenko.feature.admin.ui.schedule.ScheduleEmployeeUi

@Composable
fun ScheduleEmployeeItem(
    employee: ScheduleEmployeeUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.secondaryText()
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = employee.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                ReminderTimeRow(
                    label = "Ранок:",
                    startTime = employee.morningStartTime,
                    endTime = employee.morningEndTime,
                    enabled = employee.morningEnabled
                )
                ReminderTimeRow(
                    label = "Вечір:",
                    startTime = employee.eveningStartTime,
                    endTime = employee.eveningEndTime,
                    enabled = employee.eveningEnabled
                )
                Text(
                    text = employee.formattedDaysOfWeek,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondaryText()
                )
            }
        }
    }
}

@Composable
private fun ReminderTimeRow(
    label: String,
    startTime: String,
    endTime: String,
    enabled: Boolean
) {
    val color = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.secondaryText()
    }
    Text(
        text = "$label $startTime – $endTime",
        style = MaterialTheme.typography.bodySmall,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
private fun ScheduleEmployeeItemPreview() {
    CoreTheme {
        ScheduleEmployeeItem(
            employee = ScheduleEmployeeUi(
                id = "1",
                fullName = "Іванов Іван Іванович",
                morningEnabled = true,
                morningStartTime = "07:30",
                morningEndTime = "08:00",
                eveningEnabled = true,
                eveningStartTime = "17:30",
                eveningEndTime = "18:00",
                formattedDaysOfWeek = "Пн, Вт, Ср, Чт, Пт"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Evening disabled")
@Composable
private fun ScheduleEmployeeItemEveningDisabledPreview() {
    CoreTheme {
        ScheduleEmployeeItem(
            employee = ScheduleEmployeeUi(
                id = "2",
                fullName = "Петренко Петро Петрович",
                morningEnabled = true,
                morningStartTime = "08:00",
                morningEndTime = "08:30",
                eveningEnabled = false,
                eveningStartTime = "17:00",
                eveningEndTime = "17:30",
                formattedDaysOfWeek = "Пн, Ср, Пт"
            ),
            onClick = {}
        )
    }
}
