package com.hubenko.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.asString
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.auth.ui.components.AuthHeader
import com.hubenko.feature.auth.ui.components.CommonAuthFields
import com.hubenko.feature.auth.ui.components.LoginSubmitButton

@Composable
fun AuthContent(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { snackbarHost() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AuthHeader(isSignUp = false)

            Spacer(modifier = Modifier.height(24.dp))

            CommonAuthFields(
                email = state.email,
                password = state.pass,
                onEmailChange = { onIntent(AuthIntent.EmailChanged(it)) },
                onPasswordChange = { onIntent(AuthIntent.PasswordChanged(it)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            LoginSubmitButton(
                isLoading = state.isLoading,
                onSubmit = { onIntent(AuthIntent.Submit) }
            )

            state.error?.let { errorMessage ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage.asString(context),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Login State")
@Composable
private fun AuthContentPreview() {
    CoreTheme {
        AuthContent(
            state = AuthState(),
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

@Preview(showBackground = true, name = "Error State")
@Composable
private fun AuthContentErrorPreview() {
    CoreTheme {
        AuthContent(
            state = AuthState(error = UiText.DynamicString("Невірний email або пароль")),
            onIntent = {}
        )
    }
}
