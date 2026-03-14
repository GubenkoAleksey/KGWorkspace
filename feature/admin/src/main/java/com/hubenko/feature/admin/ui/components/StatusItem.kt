package com.hubenko.feature.admin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.domain.model.EmployeeStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusItem(
    status: EmployeeStatus,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(status.timestamp))

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = status.employeeFullName ?: "ID Працівника: ${status.employeeId}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status.status,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall
                )
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
                timestamp = System.currentTimeMillis(),
                isSynced = true
            )
        )
    }
}
