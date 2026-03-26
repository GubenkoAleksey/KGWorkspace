package com.hubenko.feature.admin.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
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
            EmployeeFormFields(
                lastName = lastName,
                onLastNameChange = { lastName = it },
                firstName = firstName,
                onFirstNameChange = { firstName = it },
                middleName = middleName,
                onMiddleNameChange = { middleName = it },
                phoneNumber = phoneNumber,
                onPhoneNumberChange = { phoneNumber = it },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                role = role,
                onRoleSelected = { role = it }
            )
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
