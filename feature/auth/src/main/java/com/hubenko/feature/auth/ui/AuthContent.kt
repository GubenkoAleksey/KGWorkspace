package com.hubenko.feature.auth.ui

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
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.feature.auth.ui.components.AuthHeader
import com.hubenko.feature.auth.ui.components.CommonAuthFields
import com.hubenko.feature.auth.ui.components.SignUpExtraFields
import com.hubenko.feature.auth.ui.components.AuthSubmitActions

@Composable
fun AuthContent(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthHeader(state.isSignUp)

        Spacer(modifier = Modifier.height(24.dp))

        CommonAuthFields(
            email = state.email,
            password = state.pass,
            onEmailChange = { onIntent(AuthIntent.EmailChanged(it)) },
            onPasswordChange = { onIntent(AuthIntent.PasswordChanged(it)) }
        )

        if (state.isSignUp) {
            SignUpExtraFields(
                lastName = state.lastName,
                firstName = state.firstName,
                middleName = state.middleName,
                phoneNumber = state.phone,
                isAdminRole = state.isAdmin,
                onLastNameChange = { onIntent(AuthIntent.LastNameChanged(it)) },
                onFirstNameChange = { onIntent(AuthIntent.FirstNameChanged(it)) },
                onMiddleNameChange = { onIntent(AuthIntent.MiddleNameChanged(it)) },
                onPhoneNumberChange = { onIntent(AuthIntent.PhoneChanged(it)) },
                onIsAdminRoleChange = { onIntent(AuthIntent.AdminRoleChanged(it)) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AuthSubmitActions(
            isSignUp = state.isSignUp,
            isLoading = state.isLoading,
            onActionSubmit = { onIntent(AuthIntent.Submit) },
            onToggleAuthMode = { onIntent(AuthIntent.ToggleAuthMode) }
        )

        state.error?.let { errorMessage ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true, name = "Login State")
@Composable
private fun AuthContentPreview() {
    CoreTheme {
        AuthContent(
            state = AuthState(isSignUp = false),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun AuthContentLoadingPreview() {
    CoreTheme {
        AuthContent(
            state = AuthState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "SignUp State")
@Composable
private fun AuthContentSignUpPreview() {
    CoreTheme {
        AuthContent(
            state = AuthState(isSignUp = true),
            onIntent = {}
        )
    }
}
