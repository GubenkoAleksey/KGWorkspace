package com.hubenko.firestoreapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
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
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Прізвище") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Ім'я") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = middleName,
            onValueChange = onMiddleNameChange,
            label = { Text("По батькові") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Номер телефону") },
            modifier = Modifier.fillMaxWidth()
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

@Preview(showBackground = true)
@Composable
fun SignUpExtraFieldsPreview() {
    SignUpExtraFields("", "", "", "", false, {}, {}, {}, {}, {})
}

@Preview(showBackground = true)
@Composable
fun AuthActionsPreview() {
    AuthActions(isSignUp = false, isLoading = false, {}, {})
}
