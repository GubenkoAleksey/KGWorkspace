package com.hubenko.feature.admin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTextField
import com.hubenko.core.ui.theme.CoreTheme
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
    var email by remember { mutableStateOf(employee?.email ?: "") }
    var password by remember { mutableStateOf(employee?.password ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (employee == null) "Додати працівника" else "Редагувати") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Прізвище",
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "Ім'я",
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = middleName,
                    onValueChange = { middleName = it },
                    label = "По батькові",
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Телефон",
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Електронна пошта",
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Пароль",
                    modifier = Modifier.fillMaxWidth()
                )
                RoleDropdown(
                    selectedRole = role,
                    onRoleSelected = { role = it },
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
                            role = role,
                            email = email,
                            password = password
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
    CoreTheme {
        EmployeeDialog(
            employee = null,
            onDismiss = {},
            onSave = {}
        )
    }
}
