package com.hubenko.feature.auth.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTextField
import com.hubenko.core.ui.theme.CoreTheme

@Composable
fun SignUpExtraFields(
    lastName: String,
    firstName: String,
    middleName: String,
    phoneNumber: String,
    isAdminRole: Boolean,
    onLastNameChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onIsAdminRoleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AppTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = "Прізвище"
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = "Ім'я"
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = middleName,
            onValueChange = onMiddleNameChange,
            label = "По батькові"
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = "Номер телефону"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isAdminRole,
                onCheckedChange = onIsAdminRoleChange
            )
            Text(text = "Реєстрація як адміністратор")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpExtraFieldsPreview() {
    CoreTheme {
        SignUpExtraFields(
            lastName = "Іванов",
            firstName = "Іван",
            middleName = "Іванович",
            phoneNumber = "+380991234567",
            isAdminRole = false,
            onLastNameChange = {},
            onFirstNameChange = {},
            onMiddleNameChange = {},
            onPhoneNumberChange = {},
            onIsAdminRoleChange = {}
        )
    }
}
