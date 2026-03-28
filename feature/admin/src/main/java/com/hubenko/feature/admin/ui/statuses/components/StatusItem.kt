package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.domain.model.EmployeeStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusItem(
    status: EmployeeStatus,
    modifier: Modifier = Modifier,
    showEmployeeName: Boolean = true
) {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val startString = sdf.format(Date(status.startTime))
    val endString = status.endTime?.let { sdf.format(Date(it)) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (showEmployeeName) {
                Text(
                    text = status.employeeFullName ?: "ID Працівника: ${status.employeeId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = "Статус: ${status.status}",
                style = MaterialTheme.typography.bodyLarge
            )
            if (!status.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Примітка: ${status.note}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Початок: $startString",
                    style = MaterialTheme.typography.labelSmall
                )
                if (endString != null) {
                    Text(
                        text = "Кінець: $endString",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusItemPreview() {
    CoreTheme {
        StatusItem(
            status = EmployeeStatus(
                id = "1",
                employeeId = "emp1",
                employeeFullName = "Іванов Іван Іванович",
                status = "Office",
                note = "Запізнюся на 10 хвилин",
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + 3600000,
                isSynced = true
            ),
            showEmployeeName = true
        )
    }
}

