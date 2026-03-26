package com.hubenko.feature.admin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTextField
import com.hubenko.core.ui.theme.CoreTheme

/**
 * Stateless composable з полями форми для введення даних співробітника.
 * Використовується всередині [EmployeeDialog].
 */
@Composable
fun EmployeeFormFields(
    lastName: String,
    onLastNameChange: (String) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    middleName: String,
    onMiddleNameChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    role: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = "Прізвище",
            modifier = Modifier.fillMaxWidth()
        )
        AppTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = "Ім'я",
            modifier = Modifier.fillMaxWidth()
        )
        AppTextField(
            value = middleName,
            onValueChange = onMiddleNameChange,
            label = "По батькові",
            modifier = Modifier.fillMaxWidth()
        )
        AppTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = "Телефон",
            modifier = Modifier.fillMaxWidth()
        )
        AppTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Електронна пошта",
            modifier = Modifier.fillMaxWidth()
        )
        AppTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Пароль",
            modifier = Modifier.fillMaxWidth()
        )
        RoleDropdown(
            selectedRole = role,
            onRoleSelected = onRoleSelected,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmployeeFormFieldsPreview() {
    CoreTheme {
        EmployeeFormFields(
            lastName = "Іваненко",
            onLastNameChange = {},
            firstName = "Іван",
            onFirstNameChange = {},
            middleName = "Іванович",
            onMiddleNameChange = {},
            phoneNumber = "+380501234567",
            onPhoneNumberChange = {},
            email = "ivan@example.com",
            onEmailChange = {},
            password = "password",
            onPasswordChange = {},
            role = "USER",
            onRoleSelected = {}
        )
    }
}
