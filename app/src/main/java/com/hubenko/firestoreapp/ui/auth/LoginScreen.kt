package com.hubenko.firestoreapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.hubenko.firestoreapp.ui.viewmodel.AuthState
import com.hubenko.firestoreapp.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (isAdmin: Boolean) -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isAdminRole by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val role = (authState as AuthState.Authenticated).role
            onLoginSuccess(role == "ADMIN" || role == "admin")
        }
    }

    LoginContent(
        isSignUp = isSignUp,
        authState = authState,
        email = email,
        password = password,
        lastName = lastName,
        firstName = firstName,
        middleName = middleName,
        phoneNumber = phoneNumber,
        isAdminRole = isAdminRole,
        onIsSignUpChange = { isSignUp = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLastNameChange = { lastName = it },
        onFirstNameChange = { firstName = it },
        onMiddleNameChange = { middleName = it },
        onPhoneNumberChange = { phoneNumber = it },
        onIsAdminRoleChange = { isAdminRole = it },
        onActionSubmit = {
            if (isSignUp) {
                viewModel.signUp(
                    email, password, lastName, firstName, middleName, phoneNumber,
                    if (isAdminRole) "ADMIN" else "USER"
                )
            } else {
                viewModel.signIn(email, password)
            }
        }
    )
}

@Composable
private fun LoginContent(
    isSignUp: Boolean,
    authState: AuthState,
    email: String,
    password: String,
    lastName: String,
    firstName: String,
    middleName: String,
    phoneNumber: String,
    isAdminRole: Boolean,
    onIsSignUpChange: (Boolean) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onIsAdminRoleChange: (Boolean) -> Unit,
    onActionSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthHeader(isSignUp)

        Spacer(modifier = Modifier.height(24.dp))

        CommonAuthFields(
            email = email,
            password = password,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange
        )

        if (isSignUp) {
            SignUpExtraFields(
                lastName = lastName,
                firstName = firstName,
                middleName = middleName,
                phoneNumber = phoneNumber,
                isAdminRole = isAdminRole,
                onLastNameChange = onLastNameChange,
                onFirstNameChange = onFirstNameChange,
                onMiddleNameChange = onMiddleNameChange,
                onPhoneNumberChange = onPhoneNumberChange,
                onIsAdminRoleChange = onIsAdminRoleChange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AuthActions(
            isSignUp = isSignUp,
            isLoading = authState is AuthState.Loading,
            onActionSubmit = onActionSubmit,
            onToggleSignUp = { onIsSignUpChange(!isSignUp) }
        )

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = authState.message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AuthHeader(isSignUp: Boolean) {
    Text(
        text = if (isSignUp) "Реєстрація" else "Вхід",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun CommonAuthFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
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

@Composable
private fun SignUpExtraFields(
    lastName: String,
    firstName: String,
    middleName: String,
    phoneNumber: String,
    isAdminRole: Boolean,
    onLastNameChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onIsAdminRoleChange: (Boolean) -> Unit
) {
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

@Composable
private fun AuthActions(
    isSignUp: Boolean,
    isLoading: Boolean,
    onActionSubmit: () -> Unit,
    onToggleSignUp: () -> Unit
) {
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
