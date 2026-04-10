package com.hubenko.feature.admin.ui.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTextField
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.employees.components.RoleDropdown
import com.hubenko.feature.admin.ui.model.RoleUi

@Composable
fun RegisterEmployeeForm(
    email: String,
    password: String,
    lastName: String,
    firstName: String,
    middleName: String,
    phone: String,
    role: String,
    roles: List<RoleUi>,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AppTextField(value = email, onValueChange = onEmailChange, label = "Email")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = password, onValueChange = onPasswordChange, label = "Пароль", visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = lastName, onValueChange = onLastNameChange, label = "Прізвище")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = firstName, onValueChange = onFirstNameChange, label = "Ім'я")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = middleName, onValueChange = onMiddleNameChange, label = "По батькові")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = phone, onValueChange = onPhoneChange, label = "Номер телефону")
        Spacer(modifier = Modifier.height(12.dp))
        RoleDropdown(
            selectedRole = role,
            roles = roles,
            onRoleSelected = onRoleChange
        )
    }
}

@Preview(showBackground = true, name = "Empty Form")
@Composable
private fun RegisterEmployeeFormEmptyPreview() {
    CoreTheme {
        RegisterEmployeeForm(
            email = "", password = "", lastName = "", firstName = "",
            middleName = "", phone = "", role = "",
            roles = listOf(RoleUi("USER", "Працівник"), RoleUi("ADMIN", "Адміністратор")),
            onEmailChange = {}, onPasswordChange = {}, onLastNameChange = {},
            onFirstNameChange = {}, onMiddleNameChange = {}, onPhoneChange = {},
            onRoleChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Admin Role")
@Composable
private fun RegisterEmployeeFormAdminPreview() {
    CoreTheme {
        RegisterEmployeeForm(
            email = "admin@company.com", password = "pass123",
            lastName = "Іванов", firstName = "Іван", middleName = "Іванович",
            phone = "+380991234567", role = "ADMIN",
            roles = listOf(RoleUi("USER", "Працівник"), RoleUi("ADMIN", "Адміністратор")),
            onEmailChange = {}, onPasswordChange = {}, onLastNameChange = {},
            onFirstNameChange = {}, onMiddleNameChange = {}, onPhoneChange = {},
            onRoleChange = {}
        )
    }
}

