package com.hubenko.feature.auth.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isAdminRole by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AuthEffect.NavigateToHome -> onNavigateToHome()
                is AuthEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
            onEmailChange = { email = it },
            onPasswordChange = { password = it }
        )

        if (isSignUp) {
            SignUpExtraFields(
                lastName = lastName,
                firstName = firstName,
                middleName = middleName,
                phoneNumber = phoneNumber,
                isAdminRole = isAdminRole,
                onLastNameChange = { lastName = it },
                onFirstNameChange = { firstName = it },
                onMiddleNameChange = { middleName = it },
                onPhoneNumberChange = { phoneNumber = it },
                onIsAdminRoleChange = { isAdminRole = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AuthActions(
            isSignUp = isSignUp,
            isLoading = state.isLoading,
            onActionSubmit = {
                if (isSignUp) {
                    viewModel.onIntent(
                        AuthIntent.SignUp(
                            email, password, lastName, firstName,
                            middleName, phoneNumber, isAdminRole
                        )
                    )
                } else {
                    viewModel.onIntent(AuthIntent.SignIn(email, password))
                }
            },
            onToggleSignUp = { isSignUp = !isSignUp }
        )

        val errorMessage = state.error
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
