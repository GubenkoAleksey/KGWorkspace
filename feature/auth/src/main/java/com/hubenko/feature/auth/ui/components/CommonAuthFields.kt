package com.hubenko.feature.auth.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTextField
import com.hubenko.core.ui.theme.CoreTheme

@Composable
fun CommonAuthFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AppTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Пароль",
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommonAuthFieldsPreview() {
    CoreTheme {
        CommonAuthFields(
            email = "employee@company.com",
            password = "password123",
            onEmailChange = {},
            onPasswordChange = {}
        )
    }
}
