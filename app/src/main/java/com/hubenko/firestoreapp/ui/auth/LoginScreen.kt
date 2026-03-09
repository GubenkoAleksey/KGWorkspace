package com.hubenko.firestoreapp.ui.auth

import androidx.compose.runtime.*
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
