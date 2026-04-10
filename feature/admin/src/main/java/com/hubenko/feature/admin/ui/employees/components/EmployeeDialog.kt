package com.hubenko.feature.admin.ui.employees.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTextField
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.model.EmployeeUi
import com.hubenko.feature.admin.ui.model.RoleUi

/**
 * Діалог редагування даних існуючого співробітника.
 *
 * @param roles Список ролей, завантажених з Firestore.
 */
@Composable
fun EmployeeDialog(
    employee: EmployeeUi?,
    roles: List<RoleUi>,
    onDismiss: () -> Unit,
    onSave: (EmployeeUi) -> Unit
) {
    var lastName by remember { mutableStateOf(employee?.lastName ?: "") }
    var firstName by remember { mutableStateOf(employee?.firstName ?: "") }
    var middleName by remember { mutableStateOf(employee?.middleName ?: "") }
    var phoneNumber by remember { mutableStateOf(employee?.phoneNumber ?: "") }
    var role by remember { mutableStateOf(employee?.role ?: "USER") }
    var email by remember { mutableStateOf(employee?.email ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Редагувати співробітника") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(value = lastName, onValueChange = { lastName = it }, label = "Прізвище", modifier = Modifier.fillMaxWidth())
                AppTextField(value = firstName, onValueChange = { firstName = it }, label = "Ім'я", modifier = Modifier.fillMaxWidth())
                AppTextField(value = middleName, onValueChange = { middleName = it }, label = "По батькові", modifier = Modifier.fillMaxWidth())
                AppTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = "Телефон", modifier = Modifier.fillMaxWidth())
                AppTextField(value = email, onValueChange = { email = it }, label = "Електронна пошта", modifier = Modifier.fillMaxWidth())
                RoleDropdown(
                    selectedRole = role,
                    roles = roles,
                    onRoleSelected = { role = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val fullName = listOf(lastName, firstName, middleName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                onSave(
                    EmployeeUi(
                        id = employee?.id ?: "",
                        lastName = lastName,
                        firstName = firstName,
                        middleName = middleName,
                        fullName = fullName,
                        phoneNumber = phoneNumber,
                        role = role,
                        email = email
                    )
                )
            }) { Text("Зберегти") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати") }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun EmployeeDialogPreview() {
    CoreTheme {
        EmployeeDialog(
            employee = EmployeeUi(
                id = "1",
                lastName = "Іванов",
                firstName = "Іван",
                middleName = "Іванович",
                fullName = "Іванов Іван Іванович",
                phoneNumber = "+380991234567",
                role = "USER",
                email = "ivan@company.com"
            ),
            roles = listOf(RoleUi("USER", "Працівник"), RoleUi("ADMIN", "Адміністратор")),
            onDismiss = {},
            onSave = {}
        )
    }
}
