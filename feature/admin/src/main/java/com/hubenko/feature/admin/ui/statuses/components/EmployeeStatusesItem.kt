package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.feature.admin.ui.statuses.EmployeeStatusesGroup

@Composable
fun EmployeeStatusesItem(
    group: EmployeeStatusesGroup,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = group.employeeName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Статусів: ${group.statuses.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (group.isExpanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (group.isExpanded) {
                        "Згорнути список статусів"
                    } else {
                        "Розгорнути список статусів"
                    }
                )
            }

            if (group.isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                group.statuses.forEach { status ->
                    StatusItem(status = status, showEmployeeName = false)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Collapsed")
@Composable
private fun EmployeeStatusesItemCollapsedPreview() {
    CoreTheme {
        EmployeeStatusesItem(
            group = EmployeeStatusesGroup(
                employeeId = "emp_1",
                employeeName = "Іванов Іван",
                statuses = previewStatuses(),
                isExpanded = false
            ),
            onToggleExpand = {}
        )
    }
}

@Preview(showBackground = true, name = "Expanded")
@Composable
private fun EmployeeStatusesItemExpandedPreview() {
    CoreTheme {
        EmployeeStatusesItem(
            group = EmployeeStatusesGroup(
                employeeId = "emp_1",
                employeeName = "Іванов Іван",
                statuses = previewStatuses(),
                isExpanded = true
            ),
            onToggleExpand = {}
        )
    }
}

@Preview(showBackground = true, name = "Expanded Empty")
@Composable
private fun EmployeeStatusesItemExpandedEmptyPreview() {
    CoreTheme {
        EmployeeStatusesItem(
            group = EmployeeStatusesGroup(
                employeeId = "emp_2",
                employeeName = "Петренко Петро",
                statuses = emptyList(),
                isExpanded = true
            ),
            onToggleExpand = {}
        )
    }
}

private fun previewStatuses(): List<EmployeeStatus> {
    return listOf(
        EmployeeStatus(
            id = "1",
            employeeId = "emp_1",
            employeeFullName = "Іванов Іван",
            status = "Office",
            note = "Працюю в офісі",
            startTime = System.currentTimeMillis(),
            endTime = null,
            isSynced = true
        ),
        EmployeeStatus(
            id = "2",
            employeeId = "emp_1",
            employeeFullName = "Іванов Іван",
            status = "Remote",
            note = null,
            startTime = System.currentTimeMillis() - 3_600_000,
            endTime = null,
            isSynced = true
        )
    )
}

