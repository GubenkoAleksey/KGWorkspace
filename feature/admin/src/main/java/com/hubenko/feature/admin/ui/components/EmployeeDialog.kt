package com.hubenko.feature.admin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hubenko.domain.model.Employee

@Composable
fun EmployeeDialog(
    employee: Employee?,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var lastName by remember { mutableStateOf(employee?.lastName ?: "") }
    var firstName by remember { mutableStateOf(employee?.firstName ?: "") }
    var middleName by remember { mutableStateOf(employee?.middleName ?: "") }
    var phoneNumber by remember { mutableStateOf(employee?.phoneNumber ?: "") }
    var role by remember { mutableStateOf(employee?.role ?: "USER") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (employee == null) "Додати працівника" else "Редагувати") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Прізвище") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Ім'я") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = middleName,
                    onValueChange = { middleName = it },
                    label = { Text("По батькові") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Роль (ADMIN/USER)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Employee(
                            id = employee?.id ?: "",
                            lastName = lastName,
                            firstName = firstName,
                            middleName = middleName,
                            phoneNumber = phoneNumber,
                            role = role
                        )
                    )
                }
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun EmployeeDialogPreview() {
    EmployeeDialog(
        employee = null,
        onDismiss = {},
        onSave = {}
    )
}
