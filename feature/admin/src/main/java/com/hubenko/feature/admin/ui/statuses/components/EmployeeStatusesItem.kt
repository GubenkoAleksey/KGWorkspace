package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.ui.model.EmployeeStatusUi
import com.hubenko.feature.admin.ui.statuses.EmployeeStatusesGroup

@Composable
fun EmployeeStatusesItem(
    group: EmployeeStatusesGroup,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
    showPayment: Boolean = true
) {
    val now = System.currentTimeMillis()
    val totalAmount = if (showPayment) group.statuses.sumOf { status ->
        val rate = group.hourlyRates[status.status] ?: 0.0
        val durationHours = ((status.endTime ?: now) - status.startTime) / 3_600_000.0
        rate * durationHours
    } else 0.0
    val isApproximate = showPayment && group.statuses.any { it.endTime == null }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                        color = MaterialTheme.colorScheme.secondaryText()
                    )
                }

                Icon(
                    imageVector = if (group.isExpanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (group.isExpanded) {
                        stringResource(R.string.cd_collapse)
                    } else {
                        stringResource(R.string.cd_expand)
                    },
                    tint = MaterialTheme.colorScheme.secondaryText()
                )
            }

            AnimatedVisibility(visible = group.isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    group.statuses.forEach { status ->
                        StatusItem(
                            status = status,
                            showEmployeeName = false,
                            hourlyRateValue = if (showPayment) group.hourlyRates[status.status] else null
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (totalAmount > 0.0) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Всього за період:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondaryText()
                    )
                    Text(
                        text = "${"%.2f".format(totalAmount)} грн${if (isApproximate) "*" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (isApproximate) {
                    Text(
                        text = "* містить незавершені статуси",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondaryText()
                    )
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

private fun previewStatuses(): List<EmployeeStatusUi> {
    return listOf(
        EmployeeStatusUi(
            id = "1",
            employeeId = "emp_1",
            employeeFullName = "Іванов Іван",
            status = "Office",
            note = "Працюю в офісі",
            startTime = System.currentTimeMillis(),
            endTime = null,
            isSynced = true
        ),
        EmployeeStatusUi(
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
