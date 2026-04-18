package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import com.hubenko.core.presentation.theme.secondaryText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.model.EmployeeStatusUi
import com.hubenko.feature.admin.ui.statuses.calculateBilledAmount
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusItem(
    status: EmployeeStatusUi,
    modifier: Modifier = Modifier,
    showEmployeeName: Boolean = true,
    hourlyRateValue: Double? = null,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val startString = sdf.format(Date(status.startTime))
    val endString = status.endTime?.let { sdf.format(Date(it)) }
    val totalAmount = hourlyRateValue?.let { calculateBilledAmount(status.startTime, status.endTime, it) }
    val isApproximate = status.endTime == null

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (onEditClick != null || onDeleteClick != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (onEditClick != null) {
                        IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редагувати",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (onDeleteClick != null) {
                        IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Видалити",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
            if (showEmployeeName) {
                Text(
                    text = status.employeeFullName ?: "ID Працівника: ${status.employeeId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = "Статус: ${status.statusLabel}",
                style = MaterialTheme.typography.bodyLarge
            )
            if (!status.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Примітка: ${status.note}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.secondaryText()
                )
            }
            if (hourlyRateValue != null && hourlyRateValue > 0.0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ставка: ${"%.2f".format(hourlyRateValue)} грн/год",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondaryText()
                )
                if (totalAmount != null) {
                    Text(
                        text = "Сума: ${"%.2f".format(totalAmount)} грн${if (isApproximate) " (орієнтовно)" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondaryText()
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Початок: $startString",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondaryText()
                )
                if (endString != null) {
                    Text(
                        text = "Кінець: $endString",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondaryText()
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
            status = EmployeeStatusUi(
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
