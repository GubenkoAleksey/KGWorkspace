package com.hubenko.feature.admin.ui.employees.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.ui.model.EmployeeUi
import com.hubenko.feature.admin.ui.model.ReminderSettingsUi

@Composable
fun EmployeeItem(
    employee: EmployeeUi,
    modifier: Modifier = Modifier,
    roleLabel: String? = null,
    baseRateLabel: String? = null,
    hourlyRateLabel: String? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReminderClick: () -> Unit = {},
    onViewStatuses: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = employee.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Тел: ${employee.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondaryText()
                )
                if (employee.email.isNotEmpty()) {
                    Text(
                        text = "Email: ${employee.email}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondaryText()
                    )
                }
                Text(
                    text = "Роль: ${roleLabel ?: employee.role}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondaryText()
                )
                if (baseRateLabel != null) {
                    Text(
                        text = "Основна ставка: $baseRateLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondaryText()
                    )
                }
                if (hourlyRateLabel != null) {
                    Text(
                        text = "Погодинна ставка: $hourlyRateLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondaryText()
                    )
                }
                employee.reminderSettings?.let { reminder ->
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    ReminderSection(reminder = reminder, onClick = onReminderClick)
                }
            }
            Column(modifier = Modifier.padding(top = 28.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_edit),
                        tint = MaterialTheme.colorScheme.secondaryText()
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cd_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onViewStatuses)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TaskAlt,
                contentDescription = stringResource(R.string.cd_view_statuses),
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.secondaryText()
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Статуси",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Перегляд і контроль статусів",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondaryText()
                )
            }
        }
    }
}

@Composable
private fun ReminderSection(reminder: ReminderSettingsUi, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Ранок: ${reminder.morningStartTime} – ${reminder.morningEndTime}",
            style = MaterialTheme.typography.bodySmall,
            color = if (reminder.morningEnabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.secondaryText()
        )
        Text(
            text = "Вечір: ${reminder.eveningStartTime} – ${reminder.eveningEndTime}",
            style = MaterialTheme.typography.bodySmall,
            color = if (reminder.eveningEnabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.secondaryText()
        )
        Text(
            text = reminder.daysOfWeek.toFormattedDays(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondaryText()
        )
    }
}

private fun List<Int>.toFormattedDays(): String {
    val names = mapOf(1 to "Нд", 2 to "Пн", 3 to "Вт", 4 to "Ср", 5 to "Чт", 6 to "Пт", 7 to "Сб")
    return sorted().mapNotNull { names[it] }.joinToString(", ")
}

@Preview(showBackground = true)
@Composable
private fun EmployeeItemPreview() {
    CoreTheme {
        EmployeeItem(
            employee = EmployeeUi(
                id = "1",
                lastName = "Іванов",
                firstName = "Іван",
                middleName = "Іванович",
                fullName = "Іванов Іван Іванович",
                phoneNumber = "+380991234567",
                role = "USER",
                email = "ivan@company.com",
                baseRateId = "1",
                baseRateValue = 7100.0,
                hourlyRateId = "",
                hourlyRateValue = 95.0,
                reminderSettings = ReminderSettingsUi(
                    employeeId = "1",
                    morningEnabled = true,
                    morningStartTime = "07:30",
                    morningEndTime = "08:00",
                    eveningEnabled = true,
                    eveningStartTime = "17:30",
                    eveningEndTime = "18:00",
                    daysOfWeek = listOf(2, 3, 4, 5, 6)
                )
            ),
            roleLabel = "Працівник",
            baseRateLabel = "7100.00 грн",
            hourlyRateLabel = "95.00 грн/год",
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true, name = "No reminder")
@Composable
private fun EmployeeItemNoReminderPreview() {
    CoreTheme {
        EmployeeItem(
            employee = EmployeeUi(
                id = "2",
                lastName = "Петренко",
                firstName = "Петро",
                middleName = "Петрович",
                fullName = "Петренко Петро Петрович",
                phoneNumber = "+380997654321",
                role = "ADMIN",
                email = ""
            ),
            roleLabel = "Адміністратор",
            onEdit = {},
            onDelete = {}
        )
    }
}
