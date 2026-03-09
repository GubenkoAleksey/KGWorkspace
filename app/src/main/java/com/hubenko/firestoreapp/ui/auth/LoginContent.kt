

package com.hubenko.firestoreapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.firestoreapp.ui.viewmodel.AuthState

@Composable
fun LoginContent(
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

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        isSignUp = false,
        authState = AuthState.Idle,
        email = "user@example.com",
        password = "password",
        lastName = "",
        firstName = "",
        middleName = "",
        phoneNumber = "",
        isAdminRole = false,
        onIsSignUpChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onLastNameChange = {},
        onFirstNameChange = {},
        onMiddleNameChange = {},
        onPhoneNumberChange = {},
        onIsAdminRoleChange = {},
        onActionSubmit = {}
    )
}
