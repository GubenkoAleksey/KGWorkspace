package com.hubenko.feature.auth.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTextField

@Composable
fun AuthHeader(isSignUp: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = if (isSignUp) "Реєстрація" else "Вхід",
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier
    )
}

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

        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Пароль",
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

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
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = "Прізвище"
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = "Ім'я"
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = middleName,
            onValueChange = onMiddleNameChange,
            label = "По батькові"
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = "Номер телефону"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAdminRole, onCheckedChange = onIsAdminRoleChange)
            Text("Роль Адміністратора")
        }
    }
}

@Composable
fun AuthActions(
    isSignUp: Boolean,
    isLoading: Boolean,
    onActionSubmit: () -> Unit,
    onToggleSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onActionSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSignUp) "Зареєструватися" else "Увійти")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onToggleSignUp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSignUp) "Вже є акаунт? Увійти" else "Немає акаунту? Реєстрація")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthHeaderPreview() {
    AuthHeader(isSignUp = false)
}

@Preview(showBackground = true)
@Composable
fun CommonAuthFieldsPreview() {
    CommonAuthFields("test@mail.com", "123456", {}, {})
}
