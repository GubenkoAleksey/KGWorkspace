package com.hubenko.feature.admin.ui.employees.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.domain.model.Employee

@Composable
fun DeleteEmployeeDialog(
    employee: Employee,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val fullName = "${employee.lastName} ${employee.firstName} ${employee.middleName}".trim()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Підтвердження видалення") },
        text = {
            Text("Видалити співробітника $fullName? Цю дію не можна скасувати.")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Видалити")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}

@Preview(showBackground = true, name = "Delete Employee Dialog")
@Composable
private fun DeleteEmployeeDialogPreview() {
    CoreTheme {
        DeleteEmployeeDialog(
            employee = Employee(
                id = "1",
                lastName = "Іванов",
                firstName = "Іван",
                middleName = "Іванович",
                phoneNumber = "+380991234567",
                role = "USER",
                email = "ivan@company.com"
            ),
            onDismiss = {},
            onConfirm = {}
        )
    }
}

